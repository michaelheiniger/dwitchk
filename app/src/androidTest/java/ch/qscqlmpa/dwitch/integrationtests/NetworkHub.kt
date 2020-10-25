package ch.qscqlmpa.dwitch.integrationtests

import ch.qscqlmpa.dwitch.GuestIdTestHost
import ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.TestWebSocket
import ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.client.IntTestWebsocketClient
import ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.server.IntTestWebsocketServer

class NetworkHub {

    private lateinit var host: IntTestWebsocketServer
    private val guests: MutableMap<GuestIdTestHost, IntTestWebsocketClient> = mutableMapOf()

    fun setHost(websocket: IntTestWebsocketServer) {
        host = websocket
    }

    fun addGuest(guest: GuestIdTestHost, websocket: IntTestWebsocketClient) {
        guests[guest] = websocket
    }

    fun sendToHost(guest: GuestIdTestHost, message: String) {
        host.onMessage(TestWebSocket(guest.ipAddress, guest.port), message)
    }

    fun broadcastToGuests(message: String) {
        guests.forEach { (_, guest) -> guest.onMessage(message) }
    }

    fun sendToGuest(guestIpAddress: String, message: String) {
        val guest = guests.keys.find { guest -> guest.ipAddress == guestIpAddress }!!
        guests.getValue(guest).onMessage(message)
    }

    fun connectToHost(guest: GuestIdTestHost) {
        host.onOpen(TestWebSocket(guest.ipAddress, guest.port), null)
        guests.getValue(guest).onOpen(null)
    }
}