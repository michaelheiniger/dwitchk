package ch.qscqlmpa.dwitch.ui.home.hostnewgame

sealed class HostNewGameDestination {
    object CurrentScreen : HostNewGameDestination()
    object NavigateToWaitingRoom : HostNewGameDestination()
}
