package ch.qscqlmpa.dwitchcommunication.websocket.client.test

import ch.qscqlmpa.dwitchcommunication.websocket.client.ClientCommEvent
import ch.qscqlmpa.dwitchcommunication.websocket.client.ClientMessage
import ch.qscqlmpa.dwitchcommunication.websocket.client.WebsocketClient
import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.core.Observable
import org.java_websocket.handshake.ServerHandshake
import org.tinylog.kotlin.Logger

internal class IntTestWebsocketClient constructor(
    @Suppress("UNUSED_PARAMETER") private val destinationAddress: String,
    @Suppress("UNUSED_PARAMETER") private val destinationPort: Int
) : WebsocketClient {

//    private val onOpenRelay = PublishRelay.create<OnOpen>()
//    private val onCloseRelay = PublishRelay.create<OnClose>()
//    private val onMessageRelay = PublishRelay.create<OnMessage>()
//    private val onErrorRelay = PublishRelay.create<OnError>()

    private val messagesSentRelay = PublishRelay.create<String>()

//    private lateinit var networkHub: NetworkHub
//    private lateinit var guestIdTestHost: PlayerHostTest
//
//    fun setNetworkHub(networkHub: NetworkHub, guestIdTestHost: PlayerHostTest) {
//        this.networkHub = networkHub
//        this.guestIdTestHost = guestIdTestHost
//    }

    private var isOpen: Boolean = false
    private var isClosed: Boolean = false

    override fun observeEvents(): Observable<ClientCommEvent> {
        TODO("Not yet implemented")
    }

    override fun observeMessages(): Observable<ClientMessage> {
        TODO("Not yet implemented")
    }

    override fun start() {
//        networkHub.connectToHost(guestIdTestHost)
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
        Logger.info { "Message sent to server: $message" }
//        networkHub.sendToHost(guestIdTestHost, message)
    }

    fun onOpen(@Suppress("UNUSED_PARAMETER") handshake: ServerHandshake?) {
        TODO("Not yet implemented")
//        onOpenRelay.accept(OnOpen(handshake))
//        isOpen = true
//        isClosed = false
    }

    fun onClose(
        @Suppress("UNUSED_PARAMETER") code: Int,
        @Suppress("UNUSED_PARAMETER") reason: String?,
        @Suppress("UNUSED_PARAMETER") remote: Boolean
    ) {
        TODO("Not yet implemented")
//        onCloseRelay.accept(OnClose(code, reason, remote))
//        isOpen = false
//        isClosed = true
    }

    fun onMessage(@Suppress("UNUSED_PARAMETER") message: String) {
        TODO("Not yet implemented")
//        onMessageRelay.accept(OnMessage(destinationAddress, destinationPort, message))
    }

    fun onError(@Suppress("UNUSED_PARAMETER") ex: Exception?) {
        TODO("Not yet implemented")
//        onErrorRelay.accept(OnError(ex))
    }

    fun observeMessagesSent(): Observable<String> {
        return messagesSentRelay
    }
}
