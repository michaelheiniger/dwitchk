package ch.qscqlmpa.dwitch.ui.home.joinnewgame

sealed class JoinNewGameCommand {
    object NavigateToWaitingRoom : JoinNewGameCommand()
}
