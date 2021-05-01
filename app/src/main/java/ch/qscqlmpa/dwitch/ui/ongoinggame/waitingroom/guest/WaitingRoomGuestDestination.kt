package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.guest

sealed class WaitingRoomGuestDestination {
    object NotifyUserGameCanceled : WaitingRoomGuestDestination()
    object NavigateToGameRoomScreen : WaitingRoomGuestDestination()
    object NavigateToHomeScreen : WaitingRoomGuestDestination()
}
