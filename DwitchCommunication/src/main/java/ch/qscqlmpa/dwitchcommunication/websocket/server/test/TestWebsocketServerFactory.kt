package ch.qscqlmpa.dwitchcommunication.websocket.server.test

import ch.qscqlmpa.dwitchcommunication.websocket.server.WebsocketServer
import ch.qscqlmpa.dwitchcommunication.websocket.server.WebsocketServerFactory

internal class TestWebsocketServerFactory : WebsocketServerFactory {

    companion object {
        private var INSTANCE: WebsocketServer? = null
    }

    override fun create(ipAddress: String, port: Int): WebsocketServer {
        if (INSTANCE == null) {
            INSTANCE = TestWebsocketServer(ipAddress, port)
        }
        return INSTANCE!!
    }
}
