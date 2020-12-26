package ch.qscqlmpa.dwitchcommunication.websocket.server

import ch.qscqlmpa.dwitchcommunication.Address
import io.reactivex.rxjava3.core.Observable
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake

internal interface WebsocketServer {

    fun observeOnOpenEvents(): Observable<OnOpen>

    fun observeOnCloseEvents(): Observable<OnClose>

    fun observeOnMessageEvents(): Observable<OnMessage>

    fun observeOnStartEvents(): Observable<OnStart>

    fun observeOnErrorEvents(): Observable<OnError>

    fun getConnections(): Collection<WebSocket>

    fun start()

    fun stop()

    fun send(websocket: WebSocket, message: String)

    fun sendBroadcast(message: String)
}

internal data class OnOpen(val conn: WebSocket?, val handshake: ClientHandshake?)
internal data class OnClose(val conn: WebSocket?, val code: Int, val reason: String?, val remote: Boolean)
internal data class OnMessage(val conn: WebSocket?, val message: String?)
internal data class OnStart(val address: Address)
internal data class OnError(val conn: WebSocket?, val ex: Exception?)

