package ch.qscqlmpa.dwitch.ui.home.joinnewgame

sealed class JoinNewGameDestination {
    object CurrentScreen : JoinNewGameDestination()
    object NavigateToHomeScreen : JoinNewGameDestination()
    object NavigateToWaitingRoom : JoinNewGameDestination()
}
