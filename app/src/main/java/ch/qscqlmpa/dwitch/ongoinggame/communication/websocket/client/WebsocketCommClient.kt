package ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.client


import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionId
import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.ClientCommunicationEvent
import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.CommClient
import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.ConnectedToHost
import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.DisconnectedFromHost
import ch.qscqlmpa.dwitch.ongoinggame.communication.serialization.SerializerFactory
import ch.qscqlmpa.dwitch.ongoinggame.messages.EnvelopeReceived
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import io.reactivex.Completable
import io.reactivex.Observable
import timber.log.Timber
import javax.inject.Inject

class WebsocketCommClient @Inject constructor(
        private val websocketClient: WebsocketClient,
        private val serializerFactory: SerializerFactory
) : CommClient {

    override fun start() {
        websocketClient.start()
    }

    override fun stop() {
        websocketClient.stop()
    }

    override fun observeCommunicationEvents(): Observable<ClientCommunicationEvent> {
        return Observable.merge(listOf(
                observeOnOpenEvents(),
                observeOnCloseEvents()
        ))
    }

    private fun observeOnOpenEvents(): Observable<ClientCommunicationEvent> {
        Timber.i("observeOnOpenEvents()")
        return websocketClient.observeOnOpenEvents()
                .doOnNext { Timber.i("Connected to Server") }
                .map { ConnectedToHost }
    }

    private fun observeOnCloseEvents(): Observable<ClientCommunicationEvent> {
        return websocketClient.observeOnCloseEvents()
                .doOnNext { Timber.i("Disconnected from Serer") }
                .map { DisconnectedFromHost }
    }

    override fun observeReceivedMessages(): Observable<EnvelopeReceived> {
        return websocketClient.observeOnMessageEvents()
                .filter { onMessage ->
                    if (onMessage.message == null) {
                        Timber.d("onMessage event filtered because message is null")
                    }
                    return@filter onMessage.message != null
                }
                .doOnNext { onMessage -> Timber.i("Message received %s", onMessage.message) }
                .map { onMessage ->
                    val message = serializerFactory.unserializeMessage(onMessage.message!!)
                    EnvelopeReceived(LocalConnectionId(0), message)
                }
    }

    override fun sendMessage(message: Message): Completable {
        return Completable.fromAction {
            val serializedMessage = serializerFactory.serialize(message)
            websocketClient.send(serializedMessage)
        }
    }
}