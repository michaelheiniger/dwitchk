package ch.qscqlmpa.dwitch.communication.websocket.server

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import java.net.InetSocketAddress


class ProdWebsocketServer constructor(address: InetSocketAddress) : WebSocketServer(address), WebsocketServer {
    constructor(hostAddress: String, hostPort: Int) : this(InetSocketAddress(hostAddress, hostPort))

    private val onStartRelay = PublishRelay.create<OnStart>()
    private val onOpenRelay = PublishRelay.create<OnOpen>()
    private val onCloseRelay = PublishRelay.create<OnClose>()
    private val onMessageRelay = PublishRelay.create<OnMessage>()
    private val onErrorRelay = PublishRelay.create<OnError>()

    override fun start() {
        super.start()
    }

    override fun stop() {
        super.stop()
    }

    override fun send(websocket: WebSocket, message: String) {
        websocket.send(message)
    }

    override fun sendBroadcast(message: String) {
        broadcast(message)
    }

    override fun onStart() {
        onStartRelay.accept(OnStart)
    }

    override fun onOpen(conn: WebSocket?, handshake: ClientHandshake?) {
        onOpenRelay.accept(OnOpen(conn, handshake))
    }

    override fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {
        onCloseRelay.accept(OnClose(conn, code, reason, remote))
    }

    override fun onMessage(conn: WebSocket?, message: String?) {
        onMessageRelay.accept(OnMessage(conn, message))
    }

    override fun onError(conn: WebSocket?, ex: Exception?) {
        onErrorRelay.accept(OnError(conn, ex))
    }

    override fun observeOnOpenEvents(): Observable<OnOpen> {
        return onOpenRelay
    }

    override fun observeOnCloseEvents(): Observable<OnClose> {
        return onCloseRelay
    }

    override fun observeOnMessageEvents(): Observable<OnMessage> {
        return onMessageRelay
    }

    override fun observeOnStartEvents(): Observable<OnStart> {
        return onStartRelay
    }

    override fun observeOnErrorEvents(): Observable<OnError> {
        return onErrorRelay
    }

    override fun getConnections(): MutableCollection<WebSocket> {
        return super.getConnections()
    }
}