package ch.qscqlmpa.dwitchcommunication.websocket.server

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId

sealed class ServerCommunicationEvent {
    data class ClientConnected(val connectionId: ConnectionId) : ServerCommunicationEvent()
    data class ClientDisconnected(val connectionId: ConnectionId?) : ServerCommunicationEvent()
    object ListeningForConnections : ServerCommunicationEvent()
    data class ErrorListeningForConnections(val exception: Exception?) : ServerCommunicationEvent()
    object NoLongerListeningForConnections : ServerCommunicationEvent()
}
