package ch.qscqlmpa.dwitchcommunication.websocket.server

internal interface WebsocketServerFactory {
    fun create(ipAddress: String, port: Int): WebsocketServer
}
