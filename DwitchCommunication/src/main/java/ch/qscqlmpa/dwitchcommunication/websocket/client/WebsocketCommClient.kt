package ch.qscqlmpa.dwitchcommunication.websocket.client

import ch.qscqlmpa.dwitchcommonutil.DisposableManager
import ch.qscqlmpa.dwitchcommunication.CommClient
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.utils.SerializerFactory
import ch.qscqlmpa.dwitchcommunication.websocket.ClientEvent
import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.core.Observable
import org.tinylog.kotlin.Logger
import javax.inject.Inject

internal class WebsocketCommClient @Inject constructor(
    private val websocketClientFactory: WebsocketClientFactory,
    private val serializerFactory: SerializerFactory
) : CommClient {

    private val disposableManager = DisposableManager()

    private lateinit var websocketClient: WebsocketClient

    private val communicationEventRelay = PublishRelay.create<ClientEvent>()

    override fun start(ipAddress: String, port: Int) {
        websocketClient = websocketClientFactory.create(ipAddress, port)

        disposableManager.add(
            websocketClient.observeEvents()
                .concatMap(::processEvent)
                .subscribe(
                    communicationEventRelay,
                    { error -> Logger.error(error) { "Error while observing communication events." } }
                )
        )

        websocketClient.start()
    }

    override fun stop() {
        if (websocketClient.isClosed()) {
            Logger.warn { "Cannot stop because the connection is already closed." }
        } else {
            websocketClient.stop()
        }
        disposableManager.disposeAndReset()
        communicationEventRelay.accept(ClientEvent.CommunicationEvent.Stopped)
    }

    override fun observeCommunicationEvents(): Observable<ClientEvent> {
        return communicationEventRelay
    }

    override fun sendMessageToServer(message: Message) {
        val serializedMessage = serializerFactory.serialize(message)
        websocketClient.send(serializedMessage)
    }

    private fun processEvent(event: ClientCommEvent): Observable<ClientEvent> {
        return when (event) {
            is ClientCommEvent.Connected -> processConnectedEvent(event)
            is ClientCommEvent.Disconnected -> processDisconnectedEvent(event)
            is ClientCommEvent.Error -> processErrorEvent(event)
            is ClientCommEvent.ClientMessage -> processMessages(event)
        }
    }

    private fun processConnectedEvent(event: ClientCommEvent.Connected): Observable<ClientEvent> {
        Logger.info { "Connected to Server ($event)" }
        return Observable.just(ClientEvent.CommunicationEvent.ConnectedToHost)
    }

    private fun processDisconnectedEvent(event: ClientCommEvent.Disconnected): Observable<ClientEvent> {
        Logger.info { "Disconnected from Server ($event)" }
        return Observable.just(ClientEvent.CommunicationEvent.DisconnectedFromHost)
    }

    private fun processErrorEvent(event: ClientCommEvent.Error): Observable<ClientEvent> {
        Logger.debug { "Communication error: $event" }
        return Observable.just(ClientEvent.CommunicationEvent.ConnectionError(event.ex?.message))
    }

    private fun processMessages(event: ClientCommEvent.ClientMessage): Observable<ClientEvent> {
        return Observable.just(event)
            .filter { envelope ->
                if (envelope.message == null) Logger.error { "onMessage event filtered because message is null" }
                envelope.message != null
            }
            .doOnNext { envelope -> Logger.info { "Message received ${envelope.message}" } }
            .map { envelope ->
                val message = serializerFactory.unserializeMessage(envelope.message!!)

                // Only one connection: the one with the Host (ID is irrelevant)
                ClientEvent.EnvelopeReceived(ConnectionId(0), message)
            }
    }
}
