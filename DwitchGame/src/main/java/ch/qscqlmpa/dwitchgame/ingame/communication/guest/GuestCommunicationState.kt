package ch.qscqlmpa.dwitchgame.ingame.communication.guest

sealed class GuestCommunicationState {
    sealed class NoConnection(open val connectedToWlan: Boolean) : GuestCommunicationState()
    data class Disconnected(override val connectedToWlan: Boolean) : NoConnection(connectedToWlan)
    data class Error(override val connectedToWlan: Boolean) : NoConnection(connectedToWlan)

    object Connecting : GuestCommunicationState()
    object Connected : GuestCommunicationState()
}
