package ch.qscqlmpa.dwitchcommunication.websocket.client.test

import ch.qscqlmpa.dwitchcommonutil.DwitchIdlingResource
import ch.qscqlmpa.dwitchcommunication.websocket.client.ClientCommEvent
import ch.qscqlmpa.dwitchcommunication.websocket.client.ClientMessage
import ch.qscqlmpa.dwitchcommunication.websocket.client.WebsocketClient
import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.java_websocket.handshake.ServerHandshake
import org.tinylog.kotlin.Logger
import java.util.concurrent.LinkedBlockingQueue

internal class TestWebsocketClient constructor(
    private val destinationAddress: String,
    private val destinationPort: Int,
    private val idlingResource: DwitchIdlingResource
) : WebsocketClient {

    private val messagesSentRelay = PublishRelay.create<String>()
    private val eventRelay = PublishRelay.create<ClientCommEvent>()
    private val messageRelay = PublishRelay.create<ClientMessage>()

    private var isOpen: Boolean = false
    private var isClosed: Boolean = false

    private val startEvent = LinkedBlockingQueue<OnStartEvent>(1)

    override fun start() {
        Completable.fromAction {
            Logger.trace { "start(): wait for start event" }
            idlingResource.increment()
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
        messagesSentRelay.accept(message)
    }

    override fun observeEvents(): Observable<ClientCommEvent> {
        return eventRelay
    }

    override fun observeMessages(): Observable<ClientMessage> {
        return messageRelay
    }

    fun onClose(code: Int, reason: String?, remote: Boolean) {
        eventRelay.accept(ClientCommEvent.Disconnected(code, reason, remote))
        isOpen = false
        isClosed = true
    }

    fun onMessage(message: String) {
        messageRelay.accept(ClientMessage(destinationAddress, destinationPort, message))
    }

    fun observeMessagesSent(): Observable<String> {
        return messagesSentRelay
    }

    fun putOnStartEvent(event: OnStartEvent) {
        Logger.debug { "putOnStartEvent: $event" }
        startEvent.put(event)
    }

    private fun onOpen(handshake: ServerHandshake?) {
        eventRelay.accept(ClientCommEvent.Connected(handshake))
        isOpen = true
        isClosed = false
    }

    private fun onError(ex: Exception?) {
        eventRelay.accept(ClientCommEvent.Error(ex))
    }
}

sealed class OnStartEvent {
    object Success : OnStartEvent()
    object Failure : OnStartEvent()
}
