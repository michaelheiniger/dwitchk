package ch.qscqlmpa.dwitchcommunication.websocket.server

internal class ProdWebsocketServerFactory constructor(
    private val hostIpAddress: String,
    private val hostPort: Int,
) : WebsocketServerFactory {

    override fun create(): WebsocketServer {
        return ProdWebsocketServer(hostIpAddress, hostPort)
    }
}