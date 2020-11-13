package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.host

sealed class GameRoomHostCommand {
    object NavigateToHomeScreen : GameRoomHostCommand()
}