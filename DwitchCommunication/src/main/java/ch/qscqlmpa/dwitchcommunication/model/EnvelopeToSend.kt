package ch.qscqlmpa.dwitchcommunication.model

import ch.qscqlmpa.dwitchcommunication.connectionstore.LocalConnectionId

data class EnvelopeToSend(val recipient: RecipientType, val message: Message)

data class EnvelopeReceived(val senderId: LocalConnectionId, val message: Message)

