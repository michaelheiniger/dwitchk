package ch.qscqlmpa.dwitchcommunication.websocket.client.test

import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.utils.SerializerFactory
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers

internal class WebsocketClientTestStub(
    private val client: TestWebsocketClient,
    private val serializerFactory: SerializerFactory
) : ClientTestStub {

    override fun connectClientToServer(enableThreadBreak: Boolean) {
        Completable.fromAction { client.onOpen(null, enableThreadBreak) }
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