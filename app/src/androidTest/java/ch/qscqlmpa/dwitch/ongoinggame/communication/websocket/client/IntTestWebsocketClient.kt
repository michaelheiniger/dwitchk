package ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.client

import ch.qscqlmpa.dwitch.PlayerHostTest
import ch.qscqlmpa.dwitch.integrationtests.NetworkHub
import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import org.java_websocket.handshake.ServerHandshake
import timber.log.Timber

class IntTestWebsocketClient constructor(
        private val destinationAddress: String,
        private val destinationPort: Int
) : WebsocketClient {

    private val onOpenRelay = PublishRelay.create<OnOpen>()
    private val onCloseRelay = PublishRelay.create<OnClose>()
    private val onMessageRelay = PublishRelay.create<OnMessage>()
    private val onErrorRelay = PublishRelay.create<OnError>()

    private val messagesSentRelay = PublishRelay.create<String>()

    private lateinit var networkHub: NetworkHub
    private lateinit var guestIdTestHost: PlayerHostTest

    fun setNetworkHub(networkHub: NetworkHub, guestIdTestHost: PlayerHostTest) {
        this.networkHub = networkHub
        this.guestIdTestHost = guestIdTestHost
    }

    private var isOpen: Boolean = false
    private var isClosed: Boolean = false

    override fun start() {
        networkHub.connectToHost(guestIdTestHost)
    }

    override fun stop() {
        onClose(1, "Connection closed manually", remote = true)
    }

    override fun isOpen(): Boolean {
        return isOpen
    }

    override fun isClosed(): Boolean {
        return isClosed
    }

    override fun send(message: String) {
        Timber.i("Message sent to server: $message")
        networkHub.sendToHost(guestIdTestHost, message)
    }

    fun onOpen(handshake: ServerHandshake?) {
        onOpenRelay.accept(OnOpen(handshake))
        isOpen = true
        isClosed = false
    }

    fun onClose(code: Int, reason: String?, remote: Boolean) {
        onCloseRelay.accept(OnClose(code, reason, remote))
        isOpen = false
        isClosed = true
    }

    fun onMessage(message: String) {
        onMessageRelay.accept(OnMessage(destinationAddress, destinationPort, message))
    }

    fun onError(ex: Exception?) {
        onErrorRelay.accept(OnError(ex))
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

    override fun observeOnErrorEvents(): Observable<OnError> {
        return onErrorRelay
    }

    fun observeMessagesSent(): Observable<String> {
        return messagesSentRelay
    }
}