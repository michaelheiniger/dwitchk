package ch.qscqlmpa.dwitchcommunication.model

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId

sealed class RecipientType {
    data class SingleGuest(val id: ConnectionId) : RecipientType()
    object Host: RecipientType()
    object All : RecipientType()
}