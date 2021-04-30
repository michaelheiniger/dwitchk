package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.host

sealed class WaitingRoomHostCommand {
    object Loading : WaitingRoomHostCommand()
    object NavigateToHomeScreen : WaitingRoomHostCommand()
    object NavigateToGameRoomScreen : WaitingRoomHostCommand()
}
