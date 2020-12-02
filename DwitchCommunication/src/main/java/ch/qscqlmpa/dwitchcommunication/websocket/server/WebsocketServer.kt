package ch.qscqlmpa.dwitchcommunication.websocket.server

import io.reactivex.rxjava3.core.Observable
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake

interface WebsocketServer {

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

data class OnOpen(val conn: WebSocket?, val handshake: ClientHandshake?)
data class OnClose(val conn: WebSocket?, val code: Int, val reason: String?, val remote: Boolean)
data class OnMessage(val conn: WebSocket?, val message: String?)
object OnStart
data class OnError(val conn: WebSocket?, val ex: Exception?)

