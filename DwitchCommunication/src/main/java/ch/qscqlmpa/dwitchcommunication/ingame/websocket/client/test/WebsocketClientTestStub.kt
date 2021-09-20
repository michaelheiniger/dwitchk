package ch.qscqlmpa.dwitchcommunication.ingame.websocket.client.test

import ch.qscqlmpa.dwitchcommunication.ingame.InGameSerializerFactory
import ch.qscqlmpa.dwitchcommunication.ingame.model.Message

internal class WebsocketClientTestStub(
    private val clientFactory: TestWebsocketClientFactory,
    private val serializerFactory: InGameSerializerFactory
) : ClientTestStub {

    private val client get() = clientFactory.getInstance()

    override fun connectClientToServer(event: OnStartEvent) = client.putOnStartEvent(event)

    override fun serverSendsMessageToClient(message: Message) {
        val messageSerialized = serializerFactory.serialize(message)
        client.onMessage(messageSerialized)
    }

    override fun serverClosesConnectionWithClient() {
        client.onClose(0, "Connection closed by server", remote = true)
    }

    override fun breakConnectionWithServer() {
        client.onClose(1, "Connection lost: unknown reason", remote = false)
    }

    override fun blockUntilMessageSentIsAvailable(): String {
        return client.blockUntilMessageSentIsAvailable()
    }
}
