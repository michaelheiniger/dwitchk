package ch.qscqlmpa.dwitchcommunication.websocket.client

import io.reactivex.rxjava3.core.Observable
import org.java_websocket.handshake.ServerHandshake

internal interface WebsocketClient {

    fun start()

    fun stop()

    fun send(message: String)

    fun isOpen(): Boolean

    fun isClosed(): Boolean

    fun observeEvents(): Observable<ClientCommEvent>
}

internal sealed class ClientCommEvent {
    internal data class Connected(val handshake: ServerHandshake?) : ClientCommEvent()
    internal data class Disconnected(val code: Int, val reason: String?, val remote: Boolean) : ClientCommEvent()
    internal data class Error(val ex: Exception?) : ClientCommEvent()
    internal data class ClientMessage(val ipAddress: String, val port: Int, val message: String?) : ClientCommEvent()
}
