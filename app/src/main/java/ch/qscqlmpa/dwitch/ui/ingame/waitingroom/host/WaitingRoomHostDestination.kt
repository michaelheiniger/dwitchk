package ch.qscqlmpa.dwitch.ui.ingame.waitingroom.host

sealed class WaitingRoomHostDestination {
    object NavigateToHomeScreen : WaitingRoomHostDestination()
    object NavigateToGameRoomScreen : WaitingRoomHostDestination()
}
