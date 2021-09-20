package ch.qscqlmpa.dwitchcommunication.ingame.websocket.server.test

import ch.qscqlmpa.dwitchcommunication.ingame.websocket.server.WebsocketServer
import ch.qscqlmpa.dwitchcommunication.ingame.websocket.server.WebsocketServerFactory

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
