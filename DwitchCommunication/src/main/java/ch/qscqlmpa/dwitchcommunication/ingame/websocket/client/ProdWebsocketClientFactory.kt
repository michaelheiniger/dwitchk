package ch.qscqlmpa.dwitchcommunication.ingame.websocket.client

import javax.inject.Inject

internal class ProdWebsocketClientFactory @Inject constructor() : WebsocketClientFactory {

    override fun create(ipAddress: String, port: Int): WebsocketClient {
        return ProdWebsocketClient(ipAddress, port)
    }
}
