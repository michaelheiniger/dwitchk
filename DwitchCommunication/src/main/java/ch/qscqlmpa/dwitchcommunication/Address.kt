package ch.qscqlmpa.dwitchcommunication

import org.java_websocket.WebSocket

data class Address(val ipAddress: String, val port: Int) {

    constructor(websocket: WebSocket) : this(
        websocket.remoteSocketAddress.address.hostAddress,
        websocket.remoteSocketAddress.port
    )
}