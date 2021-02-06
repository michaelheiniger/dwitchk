package ch.qscqlmpa.dwitchcommunication.model

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId

sealed class Recipient {
    data class Single(val id: ConnectionId) : Recipient()
    object All : Recipient()
}
