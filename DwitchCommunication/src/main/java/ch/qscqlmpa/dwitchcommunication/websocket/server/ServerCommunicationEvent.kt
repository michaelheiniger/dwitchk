package ch.qscqlmpa.dwitchcommunication.websocket.server

import ch.qscqlmpa.dwitchcommunication.connectionstore.LocalConnectionId

sealed class ServerCommunicationEvent {
    data class ClientConnected(val localConnectionId: LocalConnectionId) : ServerCommunicationEvent()
    data class ClientDisconnected(val localConnectionId: LocalConnectionId?) : ServerCommunicationEvent()
    object ListeningForConnections : ServerCommunicationEvent()
    object NotListeningForConnections : ServerCommunicationEvent()
}
