package ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.client

import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.ClientTestStub
import ch.qscqlmpa.dwitchcommunication.utils.SerializerFactory
import ch.qscqlmpa.dwitchcommunication.websocket.client.TestWebsocketClient
import ch.qscqlmpa.dwitchcommunication.model.Message
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

    override fun breakConnectionWithHost() {
        client.onClose(1, "Connection lost: unknown reason", remote = false, enableThreadBreak = true)
    }

    override fun observeMessagesSent(): Observable<String> {
        return client.observeMessagesSent()
    }
}