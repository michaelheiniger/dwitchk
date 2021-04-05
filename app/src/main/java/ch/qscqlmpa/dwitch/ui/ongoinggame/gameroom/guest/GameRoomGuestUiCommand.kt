package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.guest

sealed class GameRoomGuestUiCommand {
    object NotifyGameOver : GameRoomGuestUiCommand()
}
