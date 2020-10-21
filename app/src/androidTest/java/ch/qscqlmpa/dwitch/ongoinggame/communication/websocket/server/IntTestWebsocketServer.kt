package ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.server

import ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.server.*
import ch.qscqlmpa.dwitch.integrationtests.NetworkHub
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import org.java_websocket.WebSocket
import org.java_websocket.handshake.ClientHandshake
import java.net.InetSocketAddress


class IntTestWebsocketServer constructor(address: InetSocketAddress) : WebsocketServer {
    constructor(hostAddress: String, hostPort: Int) : this(InetSocketAddress(hostAddress, hostPort))

    private val onOpenRelay = PublishRelay.create<OnOpen>()
    private val onCloseRelay = PublishRelay.create<OnClose>()
    private val onMessageRelay = PublishRelay.create<OnMessage>()
    private val onStartRelay = PublishRelay.create<OnStart>()
    private val onErrorRelay = PublishRelay.create<OnError>()

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

    fun onStart() {
        onStartRelay.accept(OnStart)
    }

    fun onOpen(conn: WebSocket?, handshake: ClientHandshake?) {
        if (conn != null) {
            connections.add(conn)
        }
        onOpenRelay.accept(OnOpen(conn, handshake))
    }

    fun onClose(conn: WebSocket?, code: Int, reason: String?, remote: Boolean) {
        onCloseRelay.accept(OnClose(conn, code, reason, remote))
    }

    fun onMessage(conn: WebSocket?, message: String?) {
        onMessageRelay.accept(OnMessage(conn, message))
    }

    fun onError(conn: WebSocket?, ex: Exception?) {
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
}