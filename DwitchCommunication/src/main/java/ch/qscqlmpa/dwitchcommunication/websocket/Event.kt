package ch.qscqlmpa.dwitchcommunication.websocket

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message

sealed class ClientEvent {
    sealed class CommunicationEvent : ClientEvent() {
        object ConnectedToHost : CommunicationEvent()
        object DisconnectedFromHost : CommunicationEvent()
        data class ConnectionError(val error: String?) : CommunicationEvent()
        object Stopped : CommunicationEvent()
    }

    data class EnvelopeReceived(val senderId: ConnectionId, val message: Message) : ClientEvent()
}

sealed class ServerEvent {
    sealed class CommunicationEvent : ServerEvent() {
        data class ClientConnected(val connectionId: ConnectionId) : CommunicationEvent()
        data class ClientDisconnected(val connectionId: ConnectionId?) : CommunicationEvent()
        object ListeningForConnections : CommunicationEvent()
        data class ErrorListeningForConnections(val exception: Exception?) : CommunicationEvent()
        object NoLongerListeningForConnections : CommunicationEvent()
    }

    data class EnvelopeReceived(val senderId: ConnectionId, val message: Message) : ServerEvent()
}
