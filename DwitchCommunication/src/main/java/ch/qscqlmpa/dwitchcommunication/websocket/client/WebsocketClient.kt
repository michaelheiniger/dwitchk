package ch.qscqlmpa.dwitchcommunication.websocket.client

import io.reactivex.rxjava3.core.Observable
import org.java_websocket.handshake.ServerHandshake

internal interface WebsocketClient {

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

internal data class OnOpen(val handshake: ServerHandshake?)
internal data class OnClose(val code: Int, val reason: String?, val remote: Boolean)
internal data class OnMessage(val ipAddress: String, val port: Int, val message: String?)
internal data class OnError(val ex: Exception?)

