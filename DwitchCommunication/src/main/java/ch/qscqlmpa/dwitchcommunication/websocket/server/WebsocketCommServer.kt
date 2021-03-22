package ch.qscqlmpa.dwitchcommunication.websocket.server

import ch.qscqlmpa.dwitchcommonutil.DisposableManager
import ch.qscqlmpa.dwitchcommunication.Address
import ch.qscqlmpa.dwitchcommunication.AddressType
import ch.qscqlmpa.dwitchcommunication.CommServer
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStoreInternal
import ch.qscqlmpa.dwitchcommunication.model.EnvelopeReceived
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.model.Recipient
import ch.qscqlmpa.dwitchcommunication.utils.SerializerFactory
import com.jakewharton.rxrelay3.PublishRelay
import io.reactivex.rxjava3.core.Observable
import org.java_websocket.WebSocket
import org.tinylog.kotlin.Logger
import javax.inject.Inject

internal class WebsocketCommServer @Inject constructor(
    private val websocketServerFactory: WebsocketServerFactory,
    private val serializerFactory: SerializerFactory,
    private val connectionStore: ConnectionStoreInternal
) : CommServer {

    private val disposableManager = DisposableManager()

    private lateinit var websocketServer: WebsocketServer

    private val communicationEventRelay = PublishRelay.create<ServerCommunicationEvent>()
    private val messageRelay = PublishRelay.create<EnvelopeReceived>()

    override fun start() {
        websocketServer = websocketServerFactory.create()

        disposableManager.add(
            websocketServer.observeEvents()
                .flatMap(::processServerCommEvent)
                .subscribe(communicationEventRelay)
        )

        disposableManager.add(
            websocketServer.observeMessages()
                .flatMap(::processMessageEvents)
                .subscribe(messageRelay)
        )

        websocketServer.start()
    }

    override fun stop() {
        websocketServer.stop()
        disposableManager.disposeAndReset()

        // The websocket server (See class WebSocketServer) implementation doesn't provide a "stop" callback.
        communicationEventRelay.accept(ServerCommunicationEvent.NoLongerListeningForConnections)
    }

    override fun sendMessage(message: Message, recipient: Recipient) {
        val serializedMessage = serializerFactory.serialize(message)
        return when (val address = getRecipientAddress(recipient)) {
            is AddressType.Unicast -> sendUnicastMessage(serializedMessage, address)
            AddressType.Broadcast -> sendBroadcastMessage(serializedMessage)
        }
    }

    override fun observeCommunicationEvents(): Observable<ServerCommunicationEvent> {
        return communicationEventRelay
    }

    override fun observeReceivedMessages(): Observable<EnvelopeReceived> {
        return messageRelay
    }

    override fun closeConnectionWithClient(connectionId: ConnectionId) {
        val address = connectionStore.getAddress(connectionId)
        Logger.info { "Connection with remote $address closed by host." }
        if (address != null) {
            val senderSocket = websocketServer.getConnections().find { webSocket ->
                webSocket.remoteSocketAddress.address.hostAddress == address.ipAddress &&
                    webSocket.remoteSocketAddress.port == address.port
            }
            senderSocket?.close()
        }
    }

    private fun getRecipientAddress(recipient: Recipient): AddressType {
        return when (recipient) {
            is Recipient.Single -> AddressType.Unicast(connectionStore.getAddress(recipient.id)!!)
            Recipient.All -> AddressType.Broadcast
        }
    }

    private fun sendUnicastMessage(serializedMessage: String, recipientAddress: AddressType.Unicast) {
        val recipientSocket = websocketServer.getConnections().find { webSocket ->
            webSocket.remoteSocketAddress.address.hostAddress == recipientAddress.destination.ipAddress &&
                webSocket.remoteSocketAddress.port == recipientAddress.destination.port
        }
        if (recipientSocket != null) {
            websocketServer.send(recipientSocket, serializedMessage)
        } else {
            Logger.error { "Message sent to $recipientAddress but no socket found" }
        }
    }

    private fun sendBroadcastMessage(serializedMessage: String) {
        websocketServer.sendBroadcast(serializedMessage)
    }

    private fun processServerCommEvent(event: ServerCommEvent): Observable<ServerCommunicationEvent> {
        return when (event) {
            is ServerCommEvent.Started -> processStartedEvent()
            is ServerCommEvent.Error -> processErrorEvent(event)
            is ServerCommEvent.ClientConnected -> processClientConnectedEvent(event)
            is ServerCommEvent.ClientDisconnected -> processClientDisconnectedEvent(event)
        }
    }

    private fun processStartedEvent(): Observable<ServerCommunicationEvent> {
        Logger.debug { "Server is now listening for connections" }
        return Observable.just(ServerCommunicationEvent.ListeningForConnections(connectionStore.getHostConnectionId()))
    }

    private fun processClientConnectedEvent(clientConnectedEvent: ServerCommEvent.ClientConnected): Observable<ServerCommunicationEvent> {
        return Observable.just(clientConnectedEvent)
            .filter { event ->
                if (event.conn == null) {
                    Logger.debug { "OnOpen event filtered because websocket is null" }
                }
                event.conn != null
            }
            .map { event ->
                val senderAddress = buildAddressFromConnection(event.conn!!)!!
                val localConnectionId = connectionStore.addConnectionId(senderAddress)
                Logger.debug { "Client connected $senderAddress (assign local connection ID $localConnectionId)" }
                ServerCommunicationEvent.ClientConnected(localConnectionId)
            }
    }

    private fun processErrorEvent(event: ServerCommEvent.Error): Observable<ServerCommunicationEvent> {
        Logger.debug { "Communication error: $event" }
        connectionStore.clearStore()
        return Observable.just(ServerCommunicationEvent.ErrorListeningForConnections(event.ex))
    }

    private fun processClientDisconnectedEvent(clientDisconnected: ServerCommEvent.ClientDisconnected): Observable<ServerCommunicationEvent> {
        return Observable.just(clientDisconnected)
            .filter { event ->
                if (event.conn == null) {
                    Logger.debug { "OnClose event filtered because websocket is null" }
                }
                event.conn != null
            }
            .flatMap { event ->
                val senderAddress = buildAddressFromConnection(event.conn!!)
                if (senderAddress != null) {
                    Logger.debug { "Client disconnected $senderAddress (details: $event)" }
                    val localConnectionId = connectionStore.getConnectionIdForAddress(senderAddress)
                    Observable.just(ServerCommunicationEvent.ClientDisconnected(localConnectionId))
                } else {
                    val missingConnections = findMissingConnections()
                    Logger.debug { "Client disconnected but no connection info provided. Inferred missing connections: $missingConnections" }
                    Observable.fromIterable(missingConnections.map(ServerCommunicationEvent::ClientDisconnected))
                }
            }
    }

    private fun processMessageEvents(serverMessage: ServerMessage): Observable<EnvelopeReceived> {
        return Observable.just(serverMessage)
            .filter { messageEvent ->
                if (messageEvent.conn == null) {
                    Logger.debug { "onMessage event filtered because websocket is null" }
                }
                if (messageEvent.message == null) {
                    Logger.debug { "onMessage event filtered because message is null" }
                }
                messageEvent.conn != null && messageEvent.message != null
            }
            .map { messageEvent ->
                val senderAddress = buildAddressFromConnection(messageEvent.conn!!)!!
                val message = serializerFactory.unserializeMessage(messageEvent.message!!)
                val connectionId = connectionStore.getConnectionIdForAddress(senderAddress)
                    ?: throw IllegalStateException("Message received ${messageEvent.message} from $senderAddress has no connection ID")

                Logger.info { "Message received ${messageEvent.message} from $senderAddress (connection ID $connectionId)" }
                EnvelopeReceived(connectionId, message)
            }
    }

    private fun findMissingConnections(): List<ConnectionId> {
        val remainingConnections = websocketServer.getConnections()
            .filter { ws ->
                ws.remoteSocketAddress != null &&
                    ws.remoteSocketAddress.address != null &&
                    ws.remoteSocketAddress.address.hostAddress != null
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
