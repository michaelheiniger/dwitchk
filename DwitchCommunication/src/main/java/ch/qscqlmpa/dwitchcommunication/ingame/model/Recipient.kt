package ch.qscqlmpa.dwitchcommunication.ingame.model

import ch.qscqlmpa.dwitchcommunication.ingame.connectionstore.ConnectionId

sealed class Recipient {
    data class Single(val id: ConnectionId) : Recipient()
    object All : Recipient()
}
