package ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.server

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import timber.log.Timber
import java.net.InetSocketAddress


class TestWebsocketServer constructor(address: InetSocketAddress) : WebsocketServer {
    constructor(hostAddress: String, hostPort: Int) : this(InetSocketAddress(hostAddress, hostPort))

    private val onOpenRelay = PublishRelay.create<OnOpen>()
    private val onCloseRelay = PublishRelay.create<OnClose>()
    private val onMessageRelay = PublishRelay.create<OnMessage>()
    private val onStartRelay = PublishRelay.create<OnStart>()
    private val onErrorRelay = PublishRelay.create<OnError>()

    private val messageSentRelay = PublishRelay.create<String>()
    private val messageBroadcastedRelay = PublishRelay.create<String>()

    private var connections = mutableListOf<WebSocket>()

    override fun start() {
        Timber.d("start()")
        onStart(true)
    }

    override fun stop() {
        Timber.d("stop()")
    }

    override fun send(websocket: WebSocket, message: String) {
        threadBreakIfNeeded(true)
        messageSentRelay.accept(message)
    }

    override fun sendBroadcast(message: String) {
        threadBreakIfNeeded(true)
        messageSentRelay.accept(message)
    }

    fun onStart(enableThreadBreak: Boolean) {
        threadBreakIfNeeded(enableThreadBreak)
        onStartRelay.accept(OnStart)
    }

    fun onOpen(conn: WebSocket?, handshake: ClientHandshake?, enableThreadBreak: Boolean) {
        threadBreakIfNeeded(enableThreadBreak)
        if (conn != null) {
            connections.add(conn)
        }
        onOpenRelay.accept(OnOpen(conn, handshake))
    }

    fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean, enableThreadBreak: Boolean) {
        threadBreakIfNeeded(enableThreadBreak)
        onCloseRelay.accept(OnClose(conn, code, reason, remote))
    }

    fun onMessage(conn: WebSocket?, message: String?, enableThreadBreak: Boolean) {
        threadBreakIfNeeded(enableThreadBreak)
        onMessageRelay.accept(OnMessage(conn, message))
    }

    fun onError(conn: WebSocket?, ex: Exception?, enableThreadBreak: Boolean) {
        threadBreakIfNeeded(enableThreadBreak)
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

    override fun getConnections(): Collection<WebSocket> {
        return connections.toList()
    }

    fun observeMessagesSent(): Observable<String> {
        return messageSentRelay
    }

    fun observeMessagesBroadcasted(): Observable<String> {
        return messageBroadcastedRelay
    }

    private fun threadBreakIfNeeded(enableThreadBreak: Boolean) {
        if (enableThreadBreak) {
            Thread.sleep(1000)
        }
    }
}