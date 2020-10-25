package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.playerdashboard

sealed class PlayerDashboardCommand {
    object NavigateToHomeScreen : PlayerDashboardCommand()
}