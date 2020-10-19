package ch.qscqlmpa.dwitch.communication.client.websocket

import ch.qscqlmpa.dwitch.communication.client.ClientTestStub
import ch.qscqlmpa.dwitch.ongoinggame.communication.serialization.SerializerFactory
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers

class WebsocketClientTestStub(
        private val client: TestWebsocketClient,
        private val serializerFactory: SerializerFactory
) : ClientTestStub {

    override fun connectClientToServer(enableThreadBreak: Boolean) {
        Completable.fromAction {
            client.onOpen(null, enableThreadBreak)
        }
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    override fun serverSendsMessageToClient(message: Message, enableThreadBreak: Boolean) {
        val messageSerialized = serializerFactory.serialize(message)
        Completable.fromAction { client.onMessage(messageSerialized, enableThreadBreak) }
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    override fun observeMessagesSent(): Observable<String> {
        return client.observeMessagesSent()
    }
}