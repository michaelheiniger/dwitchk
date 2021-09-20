package ch.qscqlmpa.dwitchcommunication.ingame.websocket.server

internal interface WebsocketServerFactory {
    fun create(ipAddress: String, port: Int): WebsocketServer
}
