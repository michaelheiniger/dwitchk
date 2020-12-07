package ch.qscqlmpa.dwitchcommunication.websocket.server

import ch.qscqlmpa.dwitchcommunication.Address
import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.core.Observable
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import timber.log.Timber
import java.net.InetSocketAddress


internal class ProdWebsocketServer constructor(
    private val listeningAddress: String,
    private val listeningPort: Int
) : WebSocketServer(InetSocketAddress(listeningAddress, listeningPort)), WebsocketServer {

    private val onStartRelay = PublishRelay.create<OnStart>()
    private val onOpenRelay = PublishRelay.create<OnOpen>()
    private val onCloseRelay = PublishRelay.create<OnClose>()
    private val onMessageRelay = PublishRelay.create<OnMessage>()
    private val onErrorRelay = PublishRelay.create<OnError>()

    override fun start() {
        super.start()
        connectionLostTimeout = HEART_BEAT_INTERVAL_SEC
    }

    override fun stop() {
        super.stop()
    }

    override fun send(websocket: WebSocket, message: String) {
        val connectionState = websocket.readyState
        if (connectionState == WebSocket.READYSTATE.OPEN) {
            websocket.send(message)
        } else {
            Timber.e("Cannot send message when connection state is: $connectionState")
        }
    }

    override fun sendBroadcast(message: String) {
        broadcast(message)
    }

    override fun onStart() {
        onStartRelay.accept(OnStart(Address(listeningAddress, listeningPort)))
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

    companion object {
        private const val HEART_BEAT_INTERVAL_SEC = 5
    }
}