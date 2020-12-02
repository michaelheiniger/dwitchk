package ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest

import ch.qscqlmpa.dwitchgame.R


//TODO: Create mappers for resources in UI module
sealed class GuestCommunicationState {
    object Connected: GuestCommunicationState()
    object Disconnected: GuestCommunicationState()
    object Error: GuestCommunicationState()
}