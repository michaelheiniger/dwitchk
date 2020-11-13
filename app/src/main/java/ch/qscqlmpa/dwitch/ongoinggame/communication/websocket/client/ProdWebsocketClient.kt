package ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.client

import com.jakewharton.rxrelay2.PublishRelay
import io.reactivex.Observable
import org.java_websocket.WebSocket
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import timber.log.Timber
import java.net.URI
import java.util.*

class ProdWebsocketClient constructor(
        private val destinationAddress: String,
        private val destinationPort: Int
) : WebSocketClient(buildServerUri(destinationAddress, destinationPort)), WebsocketClient {

    private val onOpenRelay = PublishRelay.create<OnOpen>()
    private val onCloseRelay = PublishRelay.create<OnClose>()
    private val onMessageRelay = PublishRelay.create<OnMessage>()
    private val onErrorRelay = PublishRelay.create<OnError>()

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

    override fun isOpen(): Boolean {
        return isOpen
    }

    override fun isClosed(): Boolean {
        return isClosed
    }

    override fun onOpen(handshake: ServerHandshake?) {
        onOpenRelay.accept(OnOpen(handshake))
    }

    override fun onClose(code: Int, reason: String?, remote: Boolean) {
        onCloseRelay.accept(OnClose(code, reason, remote))
    }

    override fun onMessage(messageAsString: String?) {
        onMessageRelay.accept(OnMessage(destinationAddress, destinationPort, messageAsString))
    }

    override fun onError(ex: Exception?) {
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

    companion object {

        private const val HEART_BEAT_INTERVAL_SEC = 5

        private fun buildServerUri(destinationAddress: String, destinationPort: Int): URI {
            return URI.create(String.format(Locale.getDefault(), "ws://%s:%d", destinationAddress, destinationPort))
        }
    }
}