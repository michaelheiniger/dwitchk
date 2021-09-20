package ch.qscqlmpa.dwitchcommunication.ingame.websocket.server.test

import ch.qscqlmpa.dwitchcommunication.ingame.websocket.client.test.IntTestWebsocketClient

internal class NetworkHub {

    private lateinit var host: IntTestWebsocketServer
    private val guests: MutableMap<PlayerHostTest, IntTestWebsocketClient> = mutableMapOf()

    fun setHost(websocket: IntTestWebsocketServer) {
        host = websocket
    }

    fun addGuest(guest: PlayerHostTest, websocket: IntTestWebsocketClient) {
        guests[guest] = websocket
    }

    fun sendToHost(@Suppress("UNUSED_PARAMETER") guest: PlayerHostTest, @Suppress("UNUSED_PARAMETER") message: String) {
//        host.onMessage(TestWebSocket(guest.ipAddress, guest.port), message)
    }

    fun broadcastToGuests(message: String) {
        guests.forEach { (_, guest) -> guest.onMessage(message) }
    }

    fun sendToGuest(guestIpAddress: String, message: String) {
        val guest = guests.keys.find { guest -> guest.ipAddress == guestIpAddress }!!
        guests.getValue(guest).onMessage(message)
    }

    fun connectToHost(guest: PlayerHostTest) {
//        host.onOpen(TestWebSocket(guest.ipAddress, guest.port), null)
        guests.getValue(guest).onOpen(null)
    }
}
