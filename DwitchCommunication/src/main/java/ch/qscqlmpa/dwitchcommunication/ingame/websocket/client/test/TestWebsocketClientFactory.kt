package ch.qscqlmpa.dwitchcommunication.ingame.websocket.client.test

import ch.qscqlmpa.dwitchcommunication.ingame.websocket.client.WebsocketClient
import ch.qscqlmpa.dwitchcommunication.ingame.websocket.client.WebsocketClientFactory

internal class TestWebsocketClientFactory : WebsocketClientFactory {

    companion object {
        private var INSTANCE: TestWebsocketClient? = null
    }

    override fun create(ipAddress: String, port: Int): WebsocketClient {
        INSTANCE = TestWebsocketClient(ipAddress, port)
        return INSTANCE!!
    }

    fun getInstance(): TestWebsocketClient {
        return INSTANCE!!
    }
}
