package ch.qscqlmpa.dwitch.ui.ingame.gameroom.host

sealed class GameRoomHostDestination {
    object CurrentScreen : GameRoomHostDestination()
    object NavigateToHomeScreen : GameRoomHostDestination()
}
