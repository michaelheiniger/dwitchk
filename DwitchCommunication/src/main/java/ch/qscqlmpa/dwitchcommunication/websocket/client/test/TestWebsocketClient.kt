package ch.qscqlmpa.dwitchcommunication.websocket.client.test

import ch.qscqlmpa.dwitchcommunication.websocket.client.ClientCommEvent
import ch.qscqlmpa.dwitchcommunication.websocket.client.ClientMessage
import ch.qscqlmpa.dwitchcommunication.websocket.client.WebsocketClient
import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.core.Observable
import mu.KLogging
import org.java_websocket.handshake.ServerHandshake

internal class TestWebsocketClient constructor(
    private val destinationAddress: String,
    private val destinationPort: Int
) : WebsocketClient {

    private val messagesSentRelay = PublishRelay.create<String>()

    private val eventRelay = PublishRelay.create<ClientCommEvent>()
    private val messageRelay = PublishRelay.create<ClientMessage>()

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
        logger.info { "Message sent to server: $message" }
        threadBreakIfNeeded(true)
        messagesSentRelay.accept(message)
    }

    override fun observeEvents(): Observable<ClientCommEvent> {
        return eventRelay
    }

    override fun observeMessages(): Observable<ClientMessage> {
        return messageRelay
    }

    fun onOpen(handshake: ServerHandshake?, enableThreadBreak: Boolean) {
        threadBreakIfNeeded(enableThreadBreak)
        eventRelay.accept(ClientCommEvent.Connected(handshake))
        isOpen = true
        isClosed = false
    }

    fun onClose(code: Int, reason: String?, remote: Boolean, enableThreadBreak: Boolean) {
        threadBreakIfNeeded(enableThreadBreak)
        eventRelay.accept(ClientCommEvent.Disconnected(code, reason, remote))
        isOpen = false
        isClosed = true
    }

    fun onMessage(message: String, enableThreadBreak: Boolean) {
        threadBreakIfNeeded(enableThreadBreak)
        messageRelay.accept(ClientMessage(destinationAddress, destinationPort, message))
    }

    private fun threadBreakIfNeeded(enableThreadBreak: Boolean) {
        if (enableThreadBreak) {
            Thread.sleep(1000)
        }
    }

    fun onError(ex: Exception?) {
        Thread.sleep(1000)
        eventRelay.accept(ClientCommEvent.Error(ex))
    }

    fun observeMessagesSent(): Observable<String> {
        return messagesSentRelay
    }

    companion object : KLogging()
}
