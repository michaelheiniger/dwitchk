package ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.client

class IntTestWebsocketClientFactory constructor(
    private val hostIpAddress: String,
    private val hostPort: Int,
) : WebsocketClientFactory {

    private var instance: WebsocketClient? = null

    override fun create(): WebsocketClient {
        if (instance == null) {
            instance = IntTestWebsocketClient(hostIpAddress, hostPort)
        }
        return instance!!
    }
}