package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.guest

sealed class WaitingRoomGuestCommand {
    object NotifyUserGameCanceled : WaitingRoomGuestCommand()
    object NavigateToHomeScreen : WaitingRoomGuestCommand()
}