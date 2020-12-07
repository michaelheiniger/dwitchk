package ch.qscqlmpa.dwitchcommunication.model

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId

sealed class Recipient {
    data class SingleGuest(val id: ConnectionId) : Recipient()
    object AllGuests : Recipient()
}