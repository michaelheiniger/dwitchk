package ch.qscqlmpa.dwitchcommunication.websocket.server.test

import ch.qscqlmpa.dwitchcommunication.websocket.server.WebsocketServer
import ch.qscqlmpa.dwitchcommunication.websocket.server.WebsocketServerFactory

internal class TestWebsocketServerFactory constructor(
    private val hostIpAddress: String,
    private val hostPort: Int
) : WebsocketServerFactory {

    companion object {
        private var INSTANCE: WebsocketServer? = null
    }

    override fun create(): WebsocketServer {
        if (INSTANCE == null) {
            INSTANCE = TestWebsocketServer(hostIpAddress, hostPort)
        }
        return INSTANCE!!
    }
}
