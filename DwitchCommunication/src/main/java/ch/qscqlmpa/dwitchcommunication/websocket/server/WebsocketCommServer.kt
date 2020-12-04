package ch.qscqlmpa.dwitchcommunication.websocket.server


import ch.qscqlmpa.dwitchcommunication.Address
import ch.qscqlmpa.dwitchcommunication.AddressType
import ch.qscqlmpa.dwitchcommunication.CommServer
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.EnvelopeReceived
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.utils.SerializerFactory
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import org.java_websocket.WebSocket
import timber.log.Timber
import javax.inject.Inject

internal class WebsocketCommServer @Inject constructor(
    private val websocketServer: WebsocketServer,
    private val serializerFactory: SerializerFactory,
    private val connectionStore: ConnectionStore
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

    private fun sendUnitcastMessage(
        serializedMessage: String,
        recipientAdress: AddressType.Unicast
    ): Completable {
        return Completable.fromAction {
            val recipientSocket = websocketServer.getConnections().find { webSocket ->
                webSocket.remoteSocketAddress.address.hostAddress == recipientAdress.destination.ipAddress
                        && webSocket.remoteSocketAddress.port == recipientAdress.destination.port
            }
            if (recipientSocket != null) {
                websocketServer.send(recipientSocket, serializedMessage)
            } else {
                Timber.e("Message sent to $recipientAdress but no socket found")
            }
        }
    }

    private fun sendBroadcastMessage(serializedMessage: String): Completable {
        return Completable.fromAction { websocketServer.sendBroadcast(serializedMessage) }
    }

    override fun observeCommunicationEvents(): Observable<ServerCommunicationEvent> {
        return Observable.merge(
            listOf(
                observeOnStartEvents(),
                observeOnOpenEvents(),
                observeOnCloseEvents()
            )
        )
    }

    private fun observeOnStartEvents(): Observable<ServerCommunicationEvent> {
        return websocketServer.observeOnStartEvents()
            .map {
                Timber.d("Server is now listening for connections")
                ServerCommunicationEvent.ListeningForConnections
            }
    }

    private fun observeOnOpenEvents(): Observable<ServerCommunicationEvent> {
        return websocketServer.observeOnOpenEvents()
            .filter { onOpen ->
                if (onOpen.conn == null) {
                    Timber.d("OnOpen event filtered because websocket is null")
                }
                onOpen.conn != null
            }
            .map { onOpen ->
                val senderAddress = buildAddressFromConnection(onOpen.conn!!)!!
                val localConnectionId = connectionStore.addConnectionId(senderAddress)
                Timber.d("Client connected $senderAddress (assign local connection ID $localConnectionId)")
                ServerCommunicationEvent.ClientConnected(localConnectionId)
            }
    }

    private fun observeOnCloseEvents(): Observable<ServerCommunicationEvent> {
        return websocketServer.observeOnCloseEvents()
            .filter { onClose ->
                if (onClose.conn == null) {
                    Timber.d("OnClose event filtered because websocket is null")
                }
                onClose.conn != null
            }
            .flatMap { onClose ->
                val senderAddress = buildAddressFromConnection(onClose.conn!!)
                if (senderAddress != null) {
                    Timber.d("Client disconnected $senderAddress")
                    val localConnectionId =
                        connectionStore.getLocalConnectionIdForAddress(senderAddress)
                    return@flatMap Observable.just(ServerCommunicationEvent.ClientDisconnected(localConnectionId))
                } else {
                    val missingConnections = findMissingConnections()
                    Timber.d("Client disconnected but no connection info provided. Inferred missing connections: $missingConnections")
                    return@flatMap Observable.fromIterable(missingConnections.map(ServerCommunicationEvent::ClientDisconnected))
                }
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
                onMessage.conn != null && onMessage.message != null
            }
            .map { onMessage ->
                val senderAddress = buildAddressFromConnection(onMessage.conn!!)!!
                val message = serializerFactory.unserializeMessage(onMessage.message!!)
                val localConnectionId =
                    connectionStore.getLocalConnectionIdForAddress(senderAddress)
                        ?: throw IllegalStateException("Message received ${onMessage.message} from $senderAddress has no local connection ID")

                Timber.i(
                    "Message received %s from %s (local connection ID %s)",
                    onMessage.message,
                    senderAddress,
                    localConnectionId
                )
                EnvelopeReceived(localConnectionId, message)
            }
    }

    override fun closeConnectionWithClient(connectionId: ConnectionId) {
        val address = connectionStore.getAddress(connectionId)

        Timber.i("Connection with remote connection ID $address closed by host.")

        if (address != null) {
            val senderSocket = websocketServer.getConnections().find { webSocket ->
                webSocket.remoteSocketAddress.address.hostAddress == address.ipAddress
                        && webSocket.remoteSocketAddress.port == address.port
            }

            senderSocket?.close()
        }
    }

    private fun findMissingConnections(): List<ConnectionId> {
        val remainingConnections = websocketServer.getConnections()
            .filter { ws ->
                ws.remoteSocketAddress != null
                        && ws.remoteSocketAddress.address != null
                        && ws.remoteSocketAddress.address.hostAddress != null
            }
            .map(::Address)
        return connectionStore.findMissingConnections(remainingConnections)
    }

    private fun buildAddressFromConnection(conn: WebSocket): Address? {
        if (conn.remoteSocketAddress != null) {
            return Address(
                conn.remoteSocketAddress.address.hostAddress,
                conn.remoteSocketAddress.port
            )
        }
        return null
    }
}