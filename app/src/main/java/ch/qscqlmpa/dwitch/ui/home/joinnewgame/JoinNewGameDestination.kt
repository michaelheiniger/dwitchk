package ch.qscqlmpa.dwitch.ui.home.joinnewgame

sealed class JoinNewGameDestination {
    object NavigateToWaitingRoom : JoinNewGameDestination()
}
