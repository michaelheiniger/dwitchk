package ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest


sealed class GuestCommunicationState {
    object Connected: GuestCommunicationState()
    object Disconnected: GuestCommunicationState()
    object Error: GuestCommunicationState()
}