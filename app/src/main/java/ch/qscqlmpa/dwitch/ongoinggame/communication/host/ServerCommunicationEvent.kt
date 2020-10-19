package ch.qscqlmpa.dwitch.ongoinggame.communication.host

import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionId

sealed class ServerCommunicationEvent

data class ClientConnected(val localConnectionId: LocalConnectionId) : ServerCommunicationEvent()

data class ClientDisconnected(val localConnectionId: LocalConnectionId) : ServerCommunicationEvent()

object ListeningForConnections : ServerCommunicationEvent()

object NotListeningForConnections : ServerCommunicationEvent()