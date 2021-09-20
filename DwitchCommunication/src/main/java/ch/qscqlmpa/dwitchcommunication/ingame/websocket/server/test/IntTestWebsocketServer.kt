package ch.qscqlmpa.dwitchcommunication.ingame.websocket.server.test

import ch.qscqlmpa.dwitchcommunication.ingame.websocket.server.ServerCommEvent
import ch.qscqlmpa.dwitchcommunication.ingame.websocket.server.WebsocketServer
import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.core.Observable
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake

internal class IntTestWebsocketServer constructor(
    @Suppress("UNUSED_PARAMETER") private val hostAddress: String,
    @Suppress("UNUSED_PARAMETER") private val hostPort: Int
) : WebsocketServer {

    private val onMessageRelay = PublishRelay.create<ServerCommEvent.ServerMessage>()

    private var connections = mutableListOf<WebSocket>()

    private lateinit var networkHub: NetworkHub

    fun setNetworkHub(networkHub: NetworkHub) {
        this.networkHub = networkHub
    }

    override fun start() {
    }

    override fun stop() {
    }

    override fun send(websocket: WebSocket, message: String) {
        networkHub.sendToGuest(websocket.remoteSocketAddress.address.hostAddress, message)
    }

    override fun sendBroadcast(message: String) {
        networkHub.broadcastToGuests(message)
    }

    override fun observeEvents(): Observable<ServerCommEvent> {
        throw NotImplementedError()
    }

    fun onStart() {
//        onStartRelay.accept(OnStart(Address(hostAddress, hostPort)))
    }

    fun onOpen(conn: WebSocket?, @Suppress("UNUSED_PARAMETER") handshake: ClientHandshake?) {
        if (conn != null) {
            connections.add(conn)
        }
//        onOpenRelay.accept(OnClientConnect(conn, handshake))
    }

    fun onClose(
        @Suppress("UNUSED_PARAMETER") conn: WebSocket?,
        @Suppress("UNUSED_PARAMETER") code: Int,
        @Suppress("UNUSED_PARAMETER") reason: String?,
        @Suppress("UNUSED_PARAMETER") remote: Boolean
    ) {
//        onCloseRelay.accept(OnClientDisconnect(conn, code, reason, remote))
    }

    fun onMessage(conn: WebSocket?, message: String?) {
        onMessageRelay.accept(ServerCommEvent.ServerMessage(conn, message))
    }

    fun onError(@Suppress("UNUSED_PARAMETER") conn: WebSocket?, @Suppress("UNUSED_PARAMETER") ex: Exception?) {
//        onErrorRelay.accept(OnError(conn, ex))
    }

    override fun getConnections(): Collection<WebSocket> {
        return connections.toList()
    }
}
