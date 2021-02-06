package ch.qscqlmpa.dwitchcommunication.websocket.server

import ch.qscqlmpa.dwitchcommunication.Address
import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.core.Observable
import org.java_websocket.WebSocket
import org.java_websocket.enums.ReadyState
import org.java_websocket.handshake.ClientHandshake
import org.java_websocket.server.WebSocketServer
import timber.log.Timber
import java.net.InetSocketAddress

internal class ProdWebsocketServer constructor(
    private val listeningAddress: String,
    private val listeningPort: Int
) : WebSocketServer(InetSocketAddress(listeningAddress, listeningPort)), WebsocketServer {

    private val eventRelay = PublishRelay.create<ServerCommEvent>()
    private val messageRelay = PublishRelay.create<ServerMessage>()

    override fun start() {
        super.start()
        connectionLostTimeout = HEART_BEAT_INTERVAL_SEC
    }

    override fun send(websocket: WebSocket, message: String) {
        val connectionState = websocket.readyState
        if (connectionState == ReadyState.OPEN) {
            websocket.send(message)
        } else {
            Timber.e("Cannot send message when connection state is: $connectionState")
        }
    }

    override fun sendBroadcast(message: String) {
        broadcast(message)
    }

    override fun observeEvents(): Observable<ServerCommEvent> {
        return eventRelay
    }

    override fun observeMessages(): Observable<ServerMessage> {
        return messageRelay
    }

    override fun getConnections(): MutableCollection<WebSocket> {
        return super.getConnections()
    }

    override fun onStart() {
        eventRelay.accept(ServerCommEvent.Started(Address(listeningAddress, listeningPort)))
    }

    override fun onOpen(conn: WebSocket?, handshake: ClientHandshake?) {
        eventRelay.accept(ServerCommEvent.ClientConnected(conn, handshake))
    }

    override fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {
        eventRelay.accept(ServerCommEvent.ClientDisconnected(conn, code, reason, remote))
    }

    override fun onMessage(conn: WebSocket?, message: String?) {
        messageRelay.accept(ServerMessage(conn, message))
    }

    override fun onError(conn: WebSocket?, ex: Exception?) {
        eventRelay.accept(ServerCommEvent.Error(conn, ex))
    }

    companion object {
        private const val HEART_BEAT_INTERVAL_SEC = 5
    }
}
