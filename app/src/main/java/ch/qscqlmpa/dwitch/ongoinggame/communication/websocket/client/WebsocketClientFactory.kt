package ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.client

interface WebsocketClientFactory {

    fun create(): WebsocketClient
}