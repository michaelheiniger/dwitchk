package ch.qscqlmpa.dwitchcommunication.websocket.client

import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.core.Observable
import org.java_websocket.client.WebSocketClient
import org.java_websocket.enums.ReadyState
import org.java_websocket.handshake.ServerHandshake
import org.tinylog.kotlin.Logger
import java.net.URI
import java.util.*

internal class ProdWebsocketClient constructor(
    private val destinationAddress: String,
    private val destinationPort: Int
) : WebSocketClient(buildServerUri(destinationAddress, destinationPort)), WebsocketClient {

    private val eventRelay = PublishRelay.create<ClientCommEvent>()

    override fun start() {
        connect()
        connectionLostTimeout = HEART_BEAT_INTERVAL_SEC
    }

    override fun stop() {
        close()
    }

    override fun send(message: String) {
        if (readyState == ReadyState.OPEN) {
            super.send(message)
        } else {
            Logger.error { "Cannot send message when connection state is: $readyState" }
        }
    }

    override fun observeEvents(): Observable<ClientCommEvent> {
        return eventRelay
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
        eventRelay.accept(ClientCommEvent.ClientMessage(destinationAddress, destinationPort, messageAsString))
    }

    companion object {

        private const val HEART_BEAT_INTERVAL_SEC = 5

        private fun buildServerUri(destinationAddress: String, destinationPort: Int): URI {
            return URI.create(String.format(Locale.getDefault(), "ws://%s:%d", destinationAddress, destinationPort))
        }
    }
}
