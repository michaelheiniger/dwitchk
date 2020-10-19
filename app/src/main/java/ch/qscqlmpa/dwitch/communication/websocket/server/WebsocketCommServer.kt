package ch.qscqlmpa.dwitch.communication.websocket.server


import ch.qscqlmpa.dwitch.communication.Address
import ch.qscqlmpa.dwitch.communication.AddressType
import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionId
import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionIdStore
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.ClientConnected
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.ClientDisconnected
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.CommServer
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.ServerCommunicationEvent
import ch.qscqlmpa.dwitch.ongoinggame.communication.serialization.SerializerFactory
import ch.qscqlmpa.dwitch.ongoinggame.messages.EnvelopeReceived
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import io.reactivex.Completable
import io.reactivex.Observable
import org.java_websocket.WebSocket
import timber.log.Timber
import javax.inject.Inject

class WebsocketCommServer @Inject constructor(
    private val websocketServer: WebsocketServer,
    private val serializerFactory: SerializerFactory,
    private val connectionIdStore: LocalConnectionIdStore
) : CommServer {

    override fun start() {
        websocketServer.start()
    }

    override fun stop() {
        websocketServer.stop()
    }

    override fun sendMessage(message: Message, recipientAddress: AddressType): Completable {

        val serializedMessage = serializerFactory.serialize(message)

        return when (recipientAddress) {
            is AddressType.Unicast -> sendUnitcastMessage(serializedMessage, recipientAddress)
            AddressType.Broadcast -> sendBroadcastMessage(serializedMessage)
        }
    }

    private fun sendUnitcastMessage(serializedMessage: String, recipientAdress: AddressType.Unicast): Completable {

        return Completable.fromAction {

            val recipientSocket = websocketServer.getConnections().find { webSocket ->
                webSocket.remoteSocketAddress.address.hostAddress == recipientAdress.destination.ipAddress
                        && webSocket.remoteSocketAddress.port == recipientAdress.destination.port
            }
            if (recipientSocket != null) {
                websocketServer.send(recipientSocket, serializedMessage)
            } else {
                Timber.e("Message sent to %s but no socket found", recipientAdress)
            }
        }
    }

    private fun sendBroadcastMessage(serializedMessage: String): Completable {
        return Completable.fromAction { websocketServer.sendBroadcast(serializedMessage) }
    }

    override fun observeCommunicationEvents(): Observable<ServerCommunicationEvent> {
        return Observable.merge(listOf(
                observeOnOpenEvents(),
                observeOnCloseEvents()
        ))
    }

    private fun observeOnOpenEvents(): Observable<ServerCommunicationEvent> {
        return websocketServer.observeOnOpenEvents()
                .filter { onOpen ->
                    if (onOpen.conn == null) {
                        Timber.d("OnOpen event filtered because websocket is null")
                    }
                    return@filter onOpen.conn != null
                }
                .map { onOpen ->
                    val senderAddress = buildAddress(onOpen.conn!!)
                    val localConnectionId = connectionIdStore.addAddress(senderAddress)
                    Timber.d("Client connected %s (assign local connection ID %s)", senderAddress, localConnectionId)
                    ClientConnected(localConnectionId)
                }
    }

    private fun observeOnCloseEvents(): Observable<ServerCommunicationEvent> {
        return websocketServer.observeOnCloseEvents()
                .filter { onClose ->
                    if (onClose.conn == null) {
                        Timber.d("OnClose event filtered because websocket is null")
                    }
                    return@filter onClose.conn != null
                }
                .map { onClose ->

                    val senderAddress = buildAddress(onClose.conn!!)
                    Timber.d("Client disconnected %s", senderAddress)

                    val localConnectionId = connectionIdStore.getLocalConnectionIdForAddress(senderAddress)!!
                    ClientDisconnected(localConnectionId)
                }
    }

    override fun observeReceivedMessages(): Observable<EnvelopeReceived> {
        return websocketServer.observeOnMessageEvents()
                .filter { onMessage ->
                    if (onMessage.conn == null) {
                        Timber.d("onMessage event filtered because websocket is null")
                    }
                    if (onMessage.message == null) {
                        Timber.d("onMessage event filtered because message is null")
                    }
                    return@filter onMessage.conn != null && onMessage.message != null
                }
                .map { onMessage ->
                    val senderAddress = buildAddress(onMessage.conn!!)
                    val message = serializerFactory.unserializeMessage(onMessage.message!!)
                    val localConnectionId = connectionIdStore.getLocalConnectionIdForAddress(senderAddress)
                            ?: throw IllegalStateException("Message received ${onMessage.message} from $senderAddress has no local connection ID")

                    Timber.i("Message received %s from %s (local connection ID %s)", onMessage.message, senderAddress, localConnectionId)
                    EnvelopeReceived(localConnectionId, message)
                }
    }

    override fun closeConnectionWithClient(localConnectionId: LocalConnectionId) {

        val address = connectionIdStore.getAddress(localConnectionId)

        Timber.i("Connection with remote connection ID %s closed by host.", address)

        if (address != null) {
            val senderSocket = websocketServer.getConnections().find { webSocket ->
                webSocket.remoteSocketAddress.address.hostAddress == address.ipAddress
                        && webSocket.remoteSocketAddress.port == address.port
            }

            senderSocket?.close()
        }
    }

    private fun buildAddress(conn: WebSocket): Address {
        return Address(conn.remoteSocketAddress.address.hostAddress, conn.remoteSocketAddress.port)
    }
}