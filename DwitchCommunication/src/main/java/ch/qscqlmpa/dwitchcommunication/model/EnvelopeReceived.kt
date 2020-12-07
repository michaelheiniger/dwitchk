package ch.qscqlmpa.dwitchcommunication.model

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId

data class EnvelopeReceived(val senderId: ConnectionId, val message: Message)
