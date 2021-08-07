package ch.qscqlmpa.dwitchcommunication.websocket.server.test

import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.utils.SerializerFactory
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.java_websocket.WebSocket
import org.tinylog.kotlin.Logger

internal class WebsocketServerTestStub(
    private val server: TestWebsocketServer,
    private val serializerFactory: SerializerFactory
) : ServerTestStub {

    private val guest1Socket = TestWebSocket("192.168.1.1", 1025)
    private val guest2Socket = TestWebSocket("192.168.1.2", 1026)
    private val guest3Socket = TestWebSocket("192.168.1.3", 1027)

    override fun connectClientToServer(connectionInitiator: PlayerHostTest) {
        Completable.fromAction { server.onOpen(getSocketForGuest(connectionInitiator), handshake = null) }
            .subscribeOn(Schedulers.io())
            .subscribe(
                {},
                { error -> Logger.error(error) { "Error when connecting client to server." } }
            )
    }

    override fun clientSendsMessageToServer(sender: PlayerHostTest, message: Message) {
        val messageSerialized = serializerFactory.serialize(message)
        Completable.fromAction { server.onMessage(getSocketForGuest(sender), messageSerialized) }
            .subscribeOn(Schedulers.io())
            .subscribe(
                {},
                { error -> Logger.error(error) { "Error when client sends message to server." } }
            )
    }

    override fun blockUntilMessageSentIsAvailable(): String {
        return server.blockUntilMessageSentIsAvailable()
    }

    override fun clientDisconnectsFromServer(guestIdentifier: PlayerHostTest) {
        Completable.fromAction { server.onClose(getSocketForGuest(guestIdentifier), 1, "reason", remote = true) }
            .subscribeOn(Schedulers.io())
            .subscribe(
                {},
                { error -> Logger.error(error) { "Error when disconnecting client from server." } }
            )
    }

    private fun getSocketForGuest(guestIdentifier: PlayerHostTest): WebSocket {
        return when (guestIdentifier) {
            PlayerHostTest.Guest1 -> guest1Socket
            PlayerHostTest.Guest2 -> guest2Socket
            PlayerHostTest.Guest3 -> guest3Socket
        }
    }
}
