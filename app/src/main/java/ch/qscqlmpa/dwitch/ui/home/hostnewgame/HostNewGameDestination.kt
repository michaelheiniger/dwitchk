package ch.qscqlmpa.dwitch.ui.home.hostnewgame

sealed class HostNewGameDestination {
    object NavigateToWaitingRoom : HostNewGameDestination()
}
