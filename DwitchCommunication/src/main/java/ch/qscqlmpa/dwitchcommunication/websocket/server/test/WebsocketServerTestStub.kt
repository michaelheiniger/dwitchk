package ch.qscqlmpa.dwitchcommunication.websocket.server.test

import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.utils.SerializerFactory
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.java_websocket.WebSocket

internal class WebsocketServerTestStub(
    private val server: TestWebsocketServer,
    private val serializerFactory: SerializerFactory
) : ServerTestStub {

    private val guest1Socket = TestWebSocket("192.168.1.1", 1025)
    private val guest2Socket = TestWebSocket("192.168.1.2", 1026)
    private val guest3Socket = TestWebSocket("192.168.1.3", 1027)

    override fun connectClientToServer(connectionInitiator: PlayerHostTest) {
        Completable.fromAction { server.onOpen(getSocketForGuest(connectionInitiator), null) }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    override fun guestSendsMessageToServer(sender: PlayerHostTest, message: Message, enableThreadBreak: Boolean) {
        val messageSerialized = serializerFactory.serialize(message)
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

    override fun disconnectFromServer(guestIdentifier: PlayerHostTest, enableThreadBreak: Boolean) {
        Completable.fromAction {
            server.onClose(getSocketForGuest(guestIdentifier), 1, "reason", true, enableThreadBreak)
        }
            .subscribeOn(Schedulers.io())
            .subscribe()
    }

    private fun getSocketForGuest(guestIdentifier: PlayerHostTest): WebSocket {
        return when (guestIdentifier) {
            PlayerHostTest.Guest1 -> guest1Socket
            PlayerHostTest.Guest2 -> guest2Socket
            PlayerHostTest.Guest3 -> guest3Socket
        }
    }
}
