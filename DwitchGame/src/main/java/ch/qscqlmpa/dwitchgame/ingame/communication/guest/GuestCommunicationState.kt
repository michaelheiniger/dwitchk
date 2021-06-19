package ch.qscqlmpa.dwitchgame.ingame.communication.guest

sealed class GuestCommunicationState {
    object Disconnected : GuestCommunicationState()
    object Connecting : GuestCommunicationState()
    object Connected : GuestCommunicationState()
    object Error : GuestCommunicationState()
}
