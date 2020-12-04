package ch.qscqlmpa.dwitchcommunication.model

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId

data class EnvelopeToSend(val recipient: RecipientType, val message: Message)

data class EnvelopeReceived(val senderId: ConnectionId, val message: Message)

