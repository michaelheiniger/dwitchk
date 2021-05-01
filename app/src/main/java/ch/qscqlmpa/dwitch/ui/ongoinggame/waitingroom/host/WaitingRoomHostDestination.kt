package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.host

sealed class WaitingRoomHostDestination {
    object NavigateToHomeScreen : WaitingRoomHostDestination()
    object NavigateToGameRoomScreen : WaitingRoomHostDestination()
}
