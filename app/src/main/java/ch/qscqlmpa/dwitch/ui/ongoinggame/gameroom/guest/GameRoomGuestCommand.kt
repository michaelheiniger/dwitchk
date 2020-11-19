package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.guest

sealed class GameRoomGuestCommand {
    object ShowGameOverInfo : GameRoomGuestCommand()
    object NavigateToHomeScreen : GameRoomGuestCommand()
}