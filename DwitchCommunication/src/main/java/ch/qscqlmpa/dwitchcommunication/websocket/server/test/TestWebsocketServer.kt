package ch.qscqlmpa.dwitchcommunication.websocket.server.test

import ch.qscqlmpa.dwitchcommunication.Address
import ch.qscqlmpa.dwitchcommunication.websocket.server.ServerCommEvent
import ch.qscqlmpa.dwitchcommunication.websocket.server.ServerMessage
import ch.qscqlmpa.dwitchcommunication.websocket.server.WebsocketServer
import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.core.Observable
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.tinylog.kotlin.Logger

internal class TestWebsocketServer(
    private val hostIpAddress: String,
    private val hostPort: Int
) : WebsocketServer {

    private val eventRelay = PublishRelay.create<ServerCommEvent>()
    private val messageRelay = PublishRelay.create<ServerMessage>()

    private val messageSentRelay = PublishRelay.create<String>()
    private val messageBroadcastedRelay = PublishRelay.create<String>()

    private var connections = mutableListOf<WebSocket>()

    override fun start() {
        Logger.debug { "start()" }
        onStart()
    }

    override fun stop() {
        Logger.debug { "stop()" }
    }

    override fun send(websocket: WebSocket, message: String) {
        messageSentRelay.accept(message)
    }

    override fun sendBroadcast(message: String) {
        messageBroadcastedRelay.accept(message)
    }

    override fun observeEvents(): Observable<ServerCommEvent> {
        return eventRelay
    }

    override fun observeMessages(): Observable<ServerMessage> {
        return messageRelay
    }

    fun onStart() {
        eventRelay.accept(ServerCommEvent.Started(Address(hostIpAddress, hostPort)))
    }

    fun onOpen(conn: WebSocket?, handshake: ClientHandshake?) {
        if (conn != null) {
            connections.add(conn)
        }
        eventRelay.accept(ServerCommEvent.ClientConnected(conn, handshake))
    }

    fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {
        eventRelay.accept(ServerCommEvent.ClientDisconnected(conn, code, reason, remote))
    }

    fun onMessage(conn: WebSocket?, message: String?) {
        messageRelay.accept(ServerMessage(conn, message))
    }

    fun onError(conn: WebSocket?, ex: Exception?) {
        eventRelay.accept(ServerCommEvent.Error(conn, ex))
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
}
