package ch.qscqlmpa.dwitchcommunication.ingame.websocket

import ch.qscqlmpa.dwitchcommunication.ingame.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.ingame.model.Message

sealed class ClientEvent {
    sealed class CommunicationEvent : ClientEvent() {
        object ConnectingToServer : CommunicationEvent()
        object ConnectedToServer : CommunicationEvent()
        object DisconnectedFromServer : CommunicationEvent()
        data class ConnectionError(val error: String?) : CommunicationEvent()
        object Stopped : CommunicationEvent()
    }

    data class EnvelopeReceived(val senderId: ConnectionId, val message: Message) : ClientEvent()
}

sealed class ServerEvent {
    sealed class CommunicationEvent : ServerEvent() {
        sealed class ServerState : CommunicationEvent()
        object StartingServer : ServerState()
        object ListeningForConnections : ServerState()
        data class ErrorListeningForConnections(val exception: Exception?) : ServerState()
        object StoppedListeningForConnections : ServerState()

        data class ClientConnected(val connectionId: ConnectionId) : CommunicationEvent()
        data class ClientDisconnected(val connectionId: ConnectionId?) : CommunicationEvent()
    }

    data class EnvelopeReceived(val senderId: ConnectionId, val message: Message) : ServerEvent()
}
