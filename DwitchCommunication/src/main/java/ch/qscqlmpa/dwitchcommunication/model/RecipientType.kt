package ch.qscqlmpa.dwitchcommunication.model

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId

sealed class RecipientType {
    data class Single(val id: ConnectionId) : RecipientType()
    object All : RecipientType()
}