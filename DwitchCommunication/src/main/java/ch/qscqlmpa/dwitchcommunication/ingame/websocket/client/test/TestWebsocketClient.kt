package ch.qscqlmpa.dwitchcommunication.ingame.websocket.client.test

import ch.qscqlmpa.dwitchcommunication.ingame.websocket.client.ClientCommEvent
import ch.qscqlmpa.dwitchcommunication.ingame.websocket.client.WebsocketClient
import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.java_websocket.handshake.ServerHandshake
import org.tinylog.kotlin.Logger
import java.util.concurrent.LinkedBlockingQueue

internal class TestWebsocketClient constructor(
    private val destinationAddress: String,
    private val destinationPort: Int
) : WebsocketClient {

    private val messagesSentQueue = LinkedBlockingQueue<String>()
    private val eventRelay = PublishRelay.create<ClientCommEvent>()

    private var isOpen: Boolean = false
    private var isClosed: Boolean = false

    private val startEvent = LinkedBlockingQueue<OnStartEvent>()

    override fun start() {
        Completable.fromAction {
            Logger.debug { "start()" }
            when (startEvent.take()) {
                OnStartEvent.Failure -> onError(null)
                OnStartEvent.Success -> onOpen(null)
            }
        }.subscribeOn(Schedulers.io())
            .subscribe(
                { Logger.trace { "Websocket client started." } },
                { error -> Logger.error(error) { "Error while starting websocket client" } }
            )
    }

    override fun stop() = onClose(1, "Connection closed manually", remote = false)

    override fun isOpen(): Boolean = isOpen

    override fun isClosed(): Boolean = isClosed

    override fun send(message: String) {
        Logger.info { "Message sent to server: $message" }
        messagesSentQueue.put(message)
    }

    override fun observeEvents(): Observable<ClientCommEvent> = eventRelay

    fun onClose(code: Int, reason: String?, remote: Boolean) {
        eventRelay.accept(ClientCommEvent.Disconnected(code, reason, remote))
        isOpen = false
        isClosed = true
    }

    fun onMessage(message: String) =
        eventRelay.accept(ClientCommEvent.ClientMessage(destinationAddress, destinationPort, message))

    fun blockUntilMessageSentIsAvailable(): String = messagesSentQueue.take()

    fun putOnStartEvent(event: OnStartEvent) {
        Logger.debug { "putOnStartEvent: $event" }
        startEvent.put(event)
    }

    private fun onOpen(handshake: ServerHandshake?) {
        eventRelay.accept(ClientCommEvent.Connected(handshake))
        isOpen = true
        isClosed = false
    }

    private fun onError(ex: Exception?) = eventRelay.accept(ClientCommEvent.Error(ex))
}

sealed class OnStartEvent {
    object Success : OnStartEvent()
    object Failure : OnStartEvent()
}
