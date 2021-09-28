package ch.qscqlmpa.dwitchcommunication.ingame

import org.java_websocket.WebSocket

internal data class Address(val ipAddress: String, val port: Int) {

    constructor(websocket: WebSocket) : this(
        websocket.remoteSocketAddress.address.hostAddress ?: "",
        websocket.remoteSocketAddress.port
    )
}
