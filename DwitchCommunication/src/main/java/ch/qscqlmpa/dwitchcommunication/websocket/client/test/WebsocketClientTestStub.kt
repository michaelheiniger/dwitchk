package ch.qscqlmpa.dwitchcommunication.websocket.client.test

import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.utils.SerializerFactory
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers

internal class WebsocketClientTestStub(
    private val clientFactory: TestWebsocketClientFactory,
    private val serializerFactory: SerializerFactory
) : ClientTestStub {

    private val client get() = clientFactory.getInstance()

    override fun connectClientToServer(event: OnStartEvent) = client.putOnStartEvent(event)


    override fun serverSendsMessageToClient(message: Message) {
        val messageSerialized = serializerFactory.serialize(message)
        Completable.fromAction { client.onMessage(messageSerialized) }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    override fun breakConnectionWithServer() {
        client.onClose(1, "Connection lost: unknown reason", remote = false)
    }

    override fun blockUntilMessageSentIsAvailable(): String {
        return client.blockUntilMessageSentIsAvailable()
    }
}
