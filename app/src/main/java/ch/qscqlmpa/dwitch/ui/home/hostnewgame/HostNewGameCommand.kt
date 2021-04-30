package ch.qscqlmpa.dwitch.ui.home.hostnewgame

sealed class HostNewGameCommand {
    object Loading : HostNewGameCommand()
    object NavigateToWaitingRoom : HostNewGameCommand()
}
