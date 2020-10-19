package ch.qscqlmpa.dwitch.communication.server.websocket

import ch.qscqlmpa.dwitch.Guest1
import ch.qscqlmpa.dwitch.Guest2
import ch.qscqlmpa.dwitch.Guest3
import ch.qscqlmpa.dwitch.GuestIdTestHost
import ch.qscqlmpa.dwitch.ongoinggame.communication.serialization.SerializerFactory
import ch.qscqlmpa.dwitch.communication.server.ServerTestStub
import ch.qscqlmpa.dwitch.ongoinggame.messages.EnvelopeToSend
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.Schedulers
import org.java_websocket.WebSocket

class WebsocketServerTestStub(
        private val server: TestWebsocketServer,
        private val serializerFactory: SerializerFactory
) : ServerTestStub {

    private val guest1Socket = TestWebSocket("192.168.1.1", 1025)
    private val guest2Socket = TestWebSocket("192.168.1.2", 1026)
    private val guest3Socket = TestWebSocket("192.168.1.3", 1027)

    override fun connectClientToServer(connectionInitiator: GuestIdTestHost, enableThreadBreak: Boolean) {
        Completable.fromAction {
            server.onOpen(getSocketForGuest(connectionInitiator), null, enableThreadBreak)
        }
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    override fun guestSendsMessageToServer(sender: GuestIdTestHost, envelopeToSend: EnvelopeToSend, enableThreadBreak: Boolean) {
        val messageSerialized = serializerFactory.serialize(envelopeToSend.message)
        Completable.fromAction { server.onMessage(getSocketForGuest(sender), messageSerialized, enableThreadBreak) }
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    override fun observeMessagesSent(): Observable<String> {
        return server.observeMessagesSent()
    }

    override fun observeMessagesBroadcasted(): Observable<String> {
        return server.observeMessagesBroadcasted()
    }

    override fun disconnectFromServer(guestIdentifier: GuestIdTestHost, enableThreadBreak: Boolean) {
        Completable.fromAction {
            server.onClose(getSocketForGuest(guestIdentifier), 1, "reason", true, enableThreadBreak)
        }
                .subscribeOn(Schedulers.io())
                .subscribe()
    }

    private fun getSocketForGuest(guestIdentifier: GuestIdTestHost): WebSocket {
        return when (guestIdentifier) {
            Guest1 -> guest1Socket
            Guest2 -> guest2Socket
            Guest3 -> guest3Socket
        }
    }
}