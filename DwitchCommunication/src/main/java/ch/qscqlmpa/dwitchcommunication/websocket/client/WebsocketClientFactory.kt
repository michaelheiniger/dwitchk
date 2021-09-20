package ch.qscqlmpa.dwitchcommunication.websocket.client

internal interface WebsocketClientFactory {
    fun create(ipAddress: String, port: Int): WebsocketClient
}
