package ch.qscqlmpa.dwitchcommunication.websocket.client

interface WebsocketClientFactory {

    fun create(): WebsocketClient
}