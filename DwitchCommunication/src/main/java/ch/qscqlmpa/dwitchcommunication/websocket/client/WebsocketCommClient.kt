package ch.qscqlmpa.dwitchcommunication.websocket.client

import ch.qscqlmpa.dwitchcommonutil.DisposableManager
import ch.qscqlmpa.dwitchcommunication.CommClient
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.EnvelopeReceived
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.utils.SerializerFactory
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

    private val communicationEventRelay = PublishRelay.create<ClientCommunicationEvent>()
    private val receivedMessageRelay = PublishRelay.create<EnvelopeReceived>()

    override fun start() {
        websocketClient = websocketClientFactory.create()

        disposableManager.add(
            websocketClient.observeEvents()
                .flatMap(::processClientCommEvent)
                .subscribe(communicationEventRelay)
        )

        disposableManager.add(
            websocketClient.observeMessages()
                .flatMap(::processMessages)
                .subscribe(receivedMessageRelay)
        )

        websocketClient.start()
    }

    override fun stop() {
        if (websocketClient.isClosed()) {
            Logger.error { "Cannot stop because the connection is already closed." }
        } else {
            websocketClient.stop()
            disposableManager.disposeAndReset()
        }
    }

    override fun observeCommunicationEvents(): Observable<ClientCommunicationEvent> {
        return communicationEventRelay
    }

    override fun observeReceivedMessages(): Observable<EnvelopeReceived> {
        return receivedMessageRelay
    }

    override fun sendMessageToServer(message: Message) {
        val serializedMessage = serializerFactory.serialize(message)
        websocketClient.send(serializedMessage)
    }

    private fun processClientCommEvent(event: ClientCommEvent): Observable<ClientCommunicationEvent> {
        return when (event) {
            is ClientCommEvent.Connected -> processConnectedEvent(event)
            is ClientCommEvent.Disconnected -> processDisconnectedEvent(event)
            is ClientCommEvent.Error -> processErrorEvent(event)
        }
    }

    private fun processConnectedEvent(event: ClientCommEvent.Connected): Observable<ClientCommunicationEvent> {
        Logger.info { "Connected to Server ($event)" }
        return Observable.just(ClientCommunicationEvent.ConnectedToHost)
    }

    private fun processDisconnectedEvent(event: ClientCommEvent.Disconnected): Observable<ClientCommunicationEvent> {
        Logger.info { "Disconnected from Server ($event)" }
        return Observable.just(ClientCommunicationEvent.DisconnectedFromHost)
    }

    private fun processErrorEvent(event: ClientCommEvent.Error): Observable<ClientCommunicationEvent> {
        Logger.debug { "Communication error: $event" }
        return Observable.just(ClientCommunicationEvent.ConnectionError(event.ex?.message))
    }

    private fun processMessages(event: ClientMessage): Observable<EnvelopeReceived> {
        return Observable.just(event)
            .filter { envelope ->
                if (envelope.message == null) {
                    Logger.error { "onMessage event filtered because message is null" }
                }
                envelope.message != null
            }
            .doOnNext { envelope -> Logger.info { "Message received ${envelope.message}" } }
            .map { envelope ->
                val message = serializerFactory.unserializeMessage(envelope.message!!)

                // Only one connection: the one with the Host (ID is irrelevant)
                EnvelopeReceived(ConnectionId(0), message)
            }
    }
}
