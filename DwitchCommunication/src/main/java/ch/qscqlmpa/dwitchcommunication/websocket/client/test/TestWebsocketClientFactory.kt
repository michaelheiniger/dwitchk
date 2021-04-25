package ch.qscqlmpa.dwitchcommunication.websocket.client.test

import ch.qscqlmpa.dwitchcommonutil.DwitchIdlingResource
import ch.qscqlmpa.dwitchcommunication.websocket.client.WebsocketClient
import ch.qscqlmpa.dwitchcommunication.websocket.client.WebsocketClientFactory

internal class TestWebsocketClientFactory constructor(
    private val hostIpAddress: String,
    private val hostPort: Int,
    private val idlingResource: DwitchIdlingResource
) : WebsocketClientFactory {

    companion object {
        private var INSTANCE: WebsocketClient? = null
    }

    override fun create(): WebsocketClient {
        INSTANCE = TestWebsocketClient(hostIpAddress, hostPort, idlingResource)
        return INSTANCE!!
    }

    fun getInstance(): WebsocketClient {
        return INSTANCE!!
    }
}
