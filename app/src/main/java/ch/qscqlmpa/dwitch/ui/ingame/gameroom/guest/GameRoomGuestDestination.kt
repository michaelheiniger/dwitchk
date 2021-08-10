package ch.qscqlmpa.dwitch.ui.ingame.gameroom.guest

sealed class GameRoomGuestDestination {
    object CurrentScreen : GameRoomGuestDestination()
    object NavigateToHomeScreen : GameRoomGuestDestination()
}
