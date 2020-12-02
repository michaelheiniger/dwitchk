package ch.qscqlmpa.dwitchcommunication.websocket.client

class TestWebsocketClientFactory constructor(
    private val hostIpAddress: String,
    private val hostPort: Int
) : WebsocketClientFactory {

    companion object{
        private var INSTANCE: WebsocketClient? = null
    }

    override fun create(): WebsocketClient {
        if (INSTANCE == null) {
            INSTANCE = TestWebsocketClient(hostIpAddress, hostPort)
        }
        return INSTANCE!!
    }
}