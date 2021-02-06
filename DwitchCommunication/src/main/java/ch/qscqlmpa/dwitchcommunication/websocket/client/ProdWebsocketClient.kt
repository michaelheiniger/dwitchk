package ch.qscqlmpa.dwitchcommunication.websocket.client

import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.core.Observable
import org.java_websocket.WebSocket
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import timber.log.Timber
import java.net.URI
import java.util.*

internal class ProdWebsocketClient constructor(
    private val destinationAddress: String,
    private val destinationPort: Int
) : WebSocketClient(buildServerUri(destinationAddress, destinationPort)), WebsocketClient {

    private val eventRelay = PublishRelay.create<ClientCommEvent>()
    private val messageRelay = PublishRelay.create<ClientMessage>()

    override fun start() {
        connect()
        connectionLostTimeout = HEART_BEAT_INTERVAL_SEC
    }

    override fun stop() {
        close()
    }

    override fun send(message: String) {
        if (readyState == WebSocket.READYSTATE.OPEN) {
            super.send(message)
        } else {
            Timber.e("Cannot send message when connection state is: $readyState")
        }
    }

    override fun observeEvents(): Observable<ClientCommEvent> {
        return eventRelay
    }

    override fun observeMessages(): Observable<ClientMessage> {
        return messageRelay
    }

    override fun onOpen(handshake: ServerHandshake?) {
        eventRelay.accept(ClientCommEvent.Connected(handshake))
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        eventRelay.accept(ClientCommEvent.Disconnected(code, reason, remote))
    }

    override fun onError(ex: Exception?) {
        eventRelay.accept(ClientCommEvent.Error(ex))
    }

    override fun onMessage(messageAsString: String?) {
        messageRelay.accept(ClientMessage(destinationAddress, destinationPort, messageAsString))
    }

    companion object {

        private const val HEART_BEAT_INTERVAL_SEC = 5

        private fun buildServerUri(destinationAddress: String, destinationPort: Int): URI {
            return URI.create(String.format(Locale.getDefault(), "ws://%s:%d", destinationAddress, destinationPort))
        }
    }
}
