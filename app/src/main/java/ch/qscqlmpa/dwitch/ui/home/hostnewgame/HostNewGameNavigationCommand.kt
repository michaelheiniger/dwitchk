package ch.qscqlmpa.dwitch.ui.home.hostnewgame

sealed class HostNewGameNavigationCommand {
    object NavigateToWaitingRoom : HostNewGameNavigationCommand()
}
