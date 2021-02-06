package ch.qscqlmpa.dwitchcommunication.websocket.client

internal class ProdWebsocketClientFactory constructor(
    private val hostIpAddress: String,
    private val hostPort: Int,
) : WebsocketClientFactory {

    override fun create(): WebsocketClient {
        return ProdWebsocketClient(hostIpAddress, hostPort)
    }
}
