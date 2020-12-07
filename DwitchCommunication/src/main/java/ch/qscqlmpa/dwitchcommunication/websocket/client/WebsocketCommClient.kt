package ch.qscqlmpa.dwitchcommunication.websocket.client


import ch.qscqlmpa.dwitchcommonutil.DisposableManager
import ch.qscqlmpa.dwitchcommunication.CommClient
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.EnvelopeReceived
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.utils.SerializerFactory
import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import timber.log.Timber
import javax.inject.Inject

internal class WebsocketCommClient @Inject constructor(
    private val websocketClientFactory: WebsocketClientFactory,
    private val serializerFactory: SerializerFactory
) : CommClient {

    private var websocketClient = websocketClientFactory.create()

    private val communicationEvents = PublishRelay.create<ClientCommunicationEvent>()
    private val receivedMessages = PublishRelay.create<EnvelopeReceived>()
    private val websocketClientSubscriptions = DisposableManager()

    override fun start() {
        if (websocketClient.isOpen()) {
            throw IllegalStateException("Cannot start because a connection is already open.")
        }
        websocketClient = websocketClientFactory.create()
        subscribeToWebsocketStreams()
        websocketClient.start()
    }

    private fun subscribeToWebsocketStreams() {
        websocketClientSubscriptions.add(
            observeOnOpenEvents().subscribe(communicationEvents),
            observeOnCloseEvents().subscribe(communicationEvents),
            observeMessageEvents().subscribe(receivedMessages)
        )
    }

    override fun stop() {
        if (websocketClient.isClosed()) {
            Timber.e("Cannot stop because the connection is already closed.")
        } else {
            // We don't want to receive the events following the stopping of the websocketClient.
            websocketClientSubscriptions.disposeAndReset()
            websocketClient.stop()
        }
    }

    override fun observeCommunicationEvents(): Observable<ClientCommunicationEvent> {
        return communicationEvents
    }

    override fun observeReceivedMessages(): Observable<EnvelopeReceived> {
        return receivedMessages
    }

    override fun sendMessageToServer(message: Message): Completable {
        return Completable.fromAction {
            val serializedMessage = serializerFactory.serialize(message)
            websocketClient.send(serializedMessage)
        }
    }

    private fun observeMessageEvents(): Observable<EnvelopeReceived> {
        return websocketClient.observeOnMessageEvents()
            .filter { onMessage ->
                if (onMessage.message == null) {
                    Timber.e("onMessage event filtered because message is null")
                }
                onMessage.message != null
            }
            .doOnNext { onMessage -> Timber.i("Message received ${onMessage.message}") }
            .map { onMessage ->
                val message = serializerFactory.unserializeMessage(onMessage.message!!)

                // Only one connection: the one with the Host (ID is irrelevant)
                EnvelopeReceived(ConnectionId(0), message)
            }
    }

    private fun observeOnOpenEvents(): Observable<ClientCommunicationEvent> {
        Timber.i("Observe OnOpen events")
        return websocketClient.observeOnOpenEvents()
            .doOnNext { onOpen -> Timber.i("Connected to Server ($onOpen") }
            .map { ClientCommunicationEvent.ConnectedToHost }
    }

    private fun observeOnCloseEvents(): Observable<ClientCommunicationEvent> {
        Timber.i("Observe OnClose events")
        return websocketClient.observeOnCloseEvents()
            .doOnNext { onClose -> Timber.i("Disconnected from Server ($onClose)") }
            .map { ClientCommunicationEvent.DisconnectedFromHost }
    }
}