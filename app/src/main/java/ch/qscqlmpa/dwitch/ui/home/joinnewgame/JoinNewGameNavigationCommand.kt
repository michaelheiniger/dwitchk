package ch.qscqlmpa.dwitch.ui.home.joinnewgame

sealed class JoinNewGameNavigationCommand {
    object NavigateToWaitingRoom : JoinNewGameNavigationCommand()
}
