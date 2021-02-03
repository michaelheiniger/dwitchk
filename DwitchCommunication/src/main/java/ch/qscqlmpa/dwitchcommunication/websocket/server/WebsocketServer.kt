package ch.qscqlmpa.dwitchcommunication.websocket.server

import ch.qscqlmpa.dwitchcommunication.Address
import io.reactivex.rxjava3.core.Observable
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake

internal interface WebsocketServer {
    fun start()

    fun stop()

    fun send(websocket: WebSocket, message: String)

    fun sendBroadcast(message: String)

    fun observeEvents(): Observable<ServerCommEvent>

    fun observeMessages(): Observable<ServerMessage>

    fun getConnections(): Collection<WebSocket>
}

internal sealed class ServerCommEvent {
    internal data class ClientConnected(val conn: WebSocket?, val handshake: ClientHandshake?): ServerCommEvent()
    internal data class ClientDisconnected(val conn: WebSocket?, val code: Int, val reason: String?, val remote: Boolean): ServerCommEvent()
    internal data class Started(val address: Address): ServerCommEvent()
    internal data class Error(val conn: WebSocket?, val ex: Exception?): ServerCommEvent()
}

internal data class ServerMessage(val conn: WebSocket?, val message: String?)

