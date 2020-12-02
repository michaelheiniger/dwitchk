package ch.qscqlmpa.dwitchcommunication.model

import ch.qscqlmpa.dwitchcommunication.connectionstore.LocalConnectionId

sealed class RecipientType {
    data class Single(val localId: LocalConnectionId) : RecipientType()
    object All : RecipientType()
}