package ch.qscqlmpa.dwitchcommunication.websocket.client

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import org.java_websocket.handshake.ServerHandshake
import timber.log.Timber

class TestWebsocketClient constructor(
        private val destinationAddress: String,
        private val destinationPort: Int
) : WebsocketClient {

    private val onOpenRelay = PublishRelay.create<OnOpen>()
    private val onCloseRelay = PublishRelay.create<OnClose>()
    private val onMessageRelay = PublishRelay.create<OnMessage>()
    private val onErrorRelay = PublishRelay.create<OnError>()

    private val messagesSentRelay = PublishRelay.create<String>()

    private var isOpen: Boolean = false
    private var isClosed: Boolean = false

    override fun start() {
        // Nothing to do
    }

    override fun stop() {
        onClose(1, "Connection closed manually", remote = true, enableThreadBreak = true)
    }

    override fun isOpen(): Boolean {
        return isOpen
    }

    override fun isClosed(): Boolean {
        return isClosed
    }

    override fun send(message: String) {
        Timber.i("Message sent to server: $message")
        threadBreakIfNeeded(true)
        messagesSentRelay.accept(message)
    }

    fun onOpen(handshake: ServerHandshake?, enableThreadBreak: Boolean) {
        threadBreakIfNeeded(enableThreadBreak)
        onOpenRelay.accept(OnOpen(handshake))
        isOpen = true
        isClosed = false
    }

    fun onClose(code: Int, reason: String?, remote: Boolean, enableThreadBreak: Boolean) {
        threadBreakIfNeeded(enableThreadBreak)
        onCloseRelay.accept(OnClose(code, reason, remote))
        isOpen = false
        isClosed = true
    }

    fun onMessage(message: String, enableThreadBreak: Boolean) {
        threadBreakIfNeeded(enableThreadBreak)
        onMessageRelay.accept(OnMessage(destinationAddress, destinationPort, message))
    }

    private fun threadBreakIfNeeded(enableThreadBreak: Boolean) {
        if (enableThreadBreak) {
            Thread.sleep(1000)
        }
    }

    fun onError(ex: Exception?) {
        Thread.sleep(1000)
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