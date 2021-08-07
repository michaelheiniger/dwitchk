package ch.qscqlmpa.dwitchcommunication.websocket.server.test

import ch.qscqlmpa.dwitchcommunication.Address
import ch.qscqlmpa.dwitchcommunication.websocket.server.ServerCommEvent
import ch.qscqlmpa.dwitchcommunication.websocket.server.WebsocketServer
import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.core.Observable
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import org.tinylog.kotlin.Logger
import java.util.concurrent.LinkedBlockingQueue

internal class TestWebsocketServer(
    private val hostIpAddress: String,
    private val hostPort: Int
) : WebsocketServer {

    private val eventRelay = PublishRelay.create<ServerCommEvent>()

    private val messageSentOrBroadcastedRelay = LinkedBlockingQueue<String>()

    private var connections = mutableListOf<WebSocket>()

    override fun start() {
        Logger.debug { "start()" }
        onStart()
    }

    override fun stop() {
        Logger.debug { "stop()" }
    }

    override fun send(websocket: WebSocket, message: String) {
        Logger.info { "Message sent to client: $message ($websocket)" }
        messageSentOrBroadcastedRelay.put(message)
    }

    override fun sendBroadcast(message: String) {
        Logger.info { "Message broadcasted to clients: $message" }
        messageSentOrBroadcastedRelay.put(message)
    }

    override fun observeEvents(): Observable<ServerCommEvent> = eventRelay

    fun onStart() = eventRelay.accept(ServerCommEvent.Started(Address(hostIpAddress, hostPort)))

    fun onOpen(conn: WebSocket?, handshake: ClientHandshake?) {
        if (conn != null) {
            connections.add(conn)
        }
        eventRelay.accept(ServerCommEvent.ClientConnected(conn, handshake))
    }

    fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) =
        eventRelay.accept(ServerCommEvent.ClientDisconnected(conn, code, reason, remote))

    fun onMessage(conn: WebSocket?, message: String?) = eventRelay.accept(ServerCommEvent.ServerMessage(conn, message))

    fun onError(conn: WebSocket?, ex: Exception?) = eventRelay.accept(ServerCommEvent.Error(conn, ex))

    override fun getConnections(): Collection<WebSocket> = connections.toList()


    fun blockUntilMessageSentIsAvailable(): String = messageSentOrBroadcastedRelay.take()
}
