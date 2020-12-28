package ch.qscqlmpa.dwitchcommunication.websocket.server


import ch.qscqlmpa.dwitchcommunication.Address
import ch.qscqlmpa.dwitchcommunication.AddressType
import ch.qscqlmpa.dwitchcommunication.CommServer
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStoreInternal
import ch.qscqlmpa.dwitchcommunication.model.EnvelopeReceived
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.model.Recipient
import ch.qscqlmpa.dwitchcommunication.utils.SerializerFactory
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import org.java_websocket.WebSocket
import timber.log.Timber
import javax.inject.Inject

internal class WebsocketCommServer @Inject constructor(
    private val websocketServer: WebsocketServer,
    private val serializerFactory: SerializerFactory,
    private val connectionStore: ConnectionStoreInternal
) : CommServer {

    override fun start() {
        websocketServer.start()
    }

    override fun stop() {
        websocketServer.stop()
    }

    override fun sendMessage(message: Message, recipient: Recipient): Completable {
        val serializedMessage = serializerFactory.serialize(message)
        return when (val address = getRecipientAddress(recipient)) {
            is AddressType.Unicast -> sendUnicastMessage(serializedMessage, address)
            AddressType.Broadcast -> sendBroadcastMessage(serializedMessage)
        }
    }

    private fun getRecipientAddress(recipient: Recipient): AddressType {
        return when (recipient) {
            is Recipient.Single -> AddressType.Unicast(connectionStore.getAddress(recipient.id)!!)
            Recipient.All -> AddressType.Broadcast
        }
    }

    private fun sendUnicastMessage(serializedMessage: String, recipientAddress: AddressType.Unicast): Completable {
        return Completable.fromAction {
            val recipientSocket = websocketServer.getConnections().find { webSocket ->
                webSocket.remoteSocketAddress.address.hostAddress == recipientAddress.destination.ipAddress
                        && webSocket.remoteSocketAddress.port == recipientAddress.destination.port
            }
            if (recipientSocket != null) {
                websocketServer.send(recipientSocket, serializedMessage)
            } else {
                Timber.e("Message sent to $recipientAddress but no socket found")
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
                observeOnErrorEvents(),
                observeOnCloseEvents()
            )
        )
    }

    private fun observeOnStartEvents(): Observable<ServerCommunicationEvent.ListeningForConnections> {
        return websocketServer.observeOnStartEvents()
            .map { onStart ->
                Timber.d("Server is now listening for connections")

                // Add "virtual" connection of the server with itself
                val hostConnectionId = connectionStore.addConnectionId(onStart.address)

                ServerCommunicationEvent.ListeningForConnections(hostConnectionId)
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

    private fun observeOnErrorEvents(): Observable<ServerCommunicationEvent.ErrorListeningForConnections> {
        return websocketServer.observeOnErrorEvents()
            .map { onError -> ServerCommunicationEvent.ErrorListeningForConnections(onError.ex) }
            .doOnNext { connectionStore.clearStore() }
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
                return@flatMap if (senderAddress != null) {
                    Timber.d("Client disconnected $senderAddress")
                    val localConnectionId = connectionStore.getConnectionIdForAddress(senderAddress)
                    Observable.just(ServerCommunicationEvent.ClientDisconnected(localConnectionId))
                } else {
                    val missingConnections = findMissingConnections()
                    Timber.d("Client disconnected but no connection info provided. Inferred missing connections: $missingConnections")
                    Observable.fromIterable(missingConnections.map(ServerCommunicationEvent::ClientDisconnected))
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
                val connectionId = connectionStore.getConnectionIdForAddress(senderAddress)
                    ?: throw IllegalStateException("Message received ${onMessage.message} from $senderAddress has no connection ID")

                Timber.i("Message received %s from %s (connection ID %s)", onMessage.message, senderAddress, connectionId)
                EnvelopeReceived(connectionId, message)
            }
    }

    override fun closeConnectionWithClient(connectionId: ConnectionId) {
        val address = connectionStore.getAddress(connectionId)

        Timber.i("Connection with remote $address closed by host.")

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