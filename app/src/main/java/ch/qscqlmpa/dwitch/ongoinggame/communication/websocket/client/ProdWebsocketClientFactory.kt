package ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.client

class ProdWebsocketClientFactory constructor(
    private val hostIpAddress: String,
    private val hostPort: Int,
) : WebsocketClientFactory {

    override fun create(): WebsocketClient {
        return ProdWebsocketClient(hostIpAddress, hostPort)
    }
}