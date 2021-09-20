package ch.qscqlmpa.dwitchcommunication.websocket.server

import javax.inject.Inject

internal class ProdWebsocketServerFactory @Inject constructor() : WebsocketServerFactory {

    override fun create(ipAddress: String, port: Int): WebsocketServer {
        return ProdWebsocketServer(ipAddress, port)
    }
}
