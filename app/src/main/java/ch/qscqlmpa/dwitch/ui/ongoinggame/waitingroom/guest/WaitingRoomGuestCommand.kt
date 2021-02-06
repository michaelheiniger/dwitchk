package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.guest

sealed class WaitingRoomGuestCommand {
    object NotifyUserGameCanceled : WaitingRoomGuestCommand()
    object NavigateToGameRoomScreen : WaitingRoomGuestCommand()
    object NavigateToHomeScreen : WaitingRoomGuestCommand()
}
