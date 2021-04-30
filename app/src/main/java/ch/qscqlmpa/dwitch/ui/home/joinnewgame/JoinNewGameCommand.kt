package ch.qscqlmpa.dwitch.ui.home.joinnewgame

sealed class JoinNewGameCommand {
    object Loading : JoinNewGameCommand()
    object NavigateToWaitingRoom : JoinNewGameCommand()
}
