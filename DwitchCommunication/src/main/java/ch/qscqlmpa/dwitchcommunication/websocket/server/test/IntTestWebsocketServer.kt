package ch.qscqlmpa.dwitchcommunication.websocket.server.test

import ch.qscqlmpa.dwitchcommunication.Address
import ch.qscqlmpa.dwitchcommunication.websocket.server.*
import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.core.Observable
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake


internal class IntTestWebsocketServer constructor(
    private val hostAddress: String,
    private val hostPort: Int
    ) : WebsocketServer {

    private val onMessageRelay = PublishRelay.create<ServerMessage>()

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
        TODO("Not yet implemented")
    }

    override fun observeMessages(): Observable<ServerMessage> {
        TODO("Not yet implemented")
    }

    fun onStart() {
//        onStartRelay.accept(OnStart(Address(hostAddress, hostPort)))
    }

    fun onOpen(conn: WebSocket?, handshake: ClientHandshake?) {
        if (conn != null) {
            connections.add(conn)
        }
//        onOpenRelay.accept(OnClientConnect(conn, handshake))
    }

    fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {
//        onCloseRelay.accept(OnClientDisconnect(conn, code, reason, remote))
    }

    fun onMessage(conn: WebSocket?, message: String?) {
        onMessageRelay.accept(ServerMessage(conn, message))
    }

    fun onError(conn: WebSocket?, ex: Exception?) {
//        onErrorRelay.accept(OnError(conn, ex))
    }

    override fun getConnections(): Collection<WebSocket> {
        return connections.toList()
    }
}