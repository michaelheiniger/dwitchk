package ch.qscqlmpa.dwitchcommunication.ingame.websocket.client

internal interface WebsocketClientFactory {
    fun create(ipAddress: String, port: Int): WebsocketClient
}
