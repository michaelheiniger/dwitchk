package ch.qscqlmpa.dwitchcommunication.websocket.client.test

import ch.qscqlmpa.dwitchcommunication.websocket.client.WebsocketClient
import ch.qscqlmpa.dwitchcommunication.websocket.client.WebsocketClientFactory

internal class TestWebsocketClientFactory constructor(
    private val hostIpAddress: String,
    private val hostPort: Int
) : WebsocketClientFactory {

    companion object {
        private var INSTANCE: WebsocketClient? = null
    }

    override fun create(): WebsocketClient {
        INSTANCE = TestWebsocketClient(hostIpAddress, hostPort)
        return INSTANCE!!
    }

    fun getInstance(): WebsocketClient {
        return INSTANCE!!
    }
}
