package ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.client


import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionId
import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.ClientCommunicationEvent
import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.CommClient
import ch.qscqlmpa.dwitch.ongoinggame.communication.serialization.SerializerFactory
import ch.qscqlmpa.dwitch.ongoinggame.messages.EnvelopeReceived
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import ch.qscqlmpa.dwitch.utils.DisposableManager
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Completable
import io.reactivex.Observable
import timber.log.Timber
import javax.inject.Inject

class WebsocketCommClient @Inject constructor(
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
            throw IllegalStateException("Cannot stop because the connection is already closed.")
        }
        // We don't want to receive the events following the stopping of th websocketClient.
        websocketClientSubscriptions.disposeAndReset()
        websocketClient.stop()
    }

    override fun observeCommunicationEvents(): Observable<ClientCommunicationEvent> {
        return communicationEvents
    }

    override fun observeReceivedMessages(): Observable<EnvelopeReceived> {
        return receivedMessages
    }

    override fun sendMessage(message: Message): Completable {
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
                EnvelopeReceived(LocalConnectionId(0), message)
            }
    }

    private fun observeOnOpenEvents(): Observable<ClientCommunicationEvent> {
        Timber.i("observeOnOpenEvents()")
        return websocketClient.observeOnOpenEvents()
            .doOnNext { onOpen -> Timber.i("Connected to Server ($onOpen") }
            .map { ClientCommunicationEvent.ConnectedToHost }
    }

    private fun observeOnCloseEvents(): Observable<ClientCommunicationEvent> {
        return websocketClient.observeOnCloseEvents()
            .doOnNext { onClose -> Timber.i("Disconnected from Server ($onClose)") }
            .map { ClientCommunicationEvent.DisconnectedFromHost }
    }
}