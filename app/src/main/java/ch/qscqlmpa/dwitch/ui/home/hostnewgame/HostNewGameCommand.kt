package ch.qscqlmpa.dwitch.ui.home.hostnewgame

sealed class HostNewGameCommand {
    object NavigateToWaitingRoom: HostNewGameCommand()
}