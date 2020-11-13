package ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.client

import io.reactivex.Observable
import org.java_websocket.handshake.ServerHandshake

interface WebsocketClient {

    fun start()

    fun stop()

    fun send(message: String)

    fun isOpen(): Boolean

    fun isClosed(): Boolean

    fun observeOnOpenEvents(): Observable<OnOpen>

    fun observeOnCloseEvents(): Observable<OnClose>

    fun observeOnMessageEvents(): Observable<OnMessage>

    fun observeOnErrorEvents(): Observable<OnError>
}

data class OnOpen(val handshake: ServerHandshake?)
data class OnClose(val code: Int, val reason: String?, val remote: Boolean)
data class OnMessage(val ipAddress: String, val port: Int, val message: String?)
data class OnError(val ex: Exception?)

