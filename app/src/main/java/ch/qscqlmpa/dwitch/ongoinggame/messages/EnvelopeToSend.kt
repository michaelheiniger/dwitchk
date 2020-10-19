package ch.qscqlmpa.dwitch.ongoinggame.messages

import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionId
import ch.qscqlmpa.dwitch.ongoinggame.communication.RecipientType

data class EnvelopeToSend(val recipient: RecipientType, val message: Message)

data class EnvelopeReceived(val senderId: LocalConnectionId, val message: Message)

