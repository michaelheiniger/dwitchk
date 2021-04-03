package ch.qscqlmpa.dwitch.ui.ongoinggame.endofround

sealed class EndOfRoundHostCommand {
    object NavigateHome : EndOfRoundHostCommand()
    object NavigateToGameRoom : EndOfRoundHostCommand()
}

sealed class EndOfRoundGuestCommand {
    object NavigateHome : EndOfRoundGuestCommand()
}
