package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.guest

import DashboardScreen
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ch.qscqlmpa.dwitch.ui.ConnectionGuestScreen
import ch.qscqlmpa.dwitch.ui.ongoinggame.GameOverDialog
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.endofround.EndOfRoundScreen
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicationState
import ch.qscqlmpa.dwitchgame.ongoinggame.game.EndOfRoundInfo
import ch.qscqlmpa.dwitchgame.ongoinggame.game.GameDashboardInfo

@Composable
fun GameRoomGuestScreen(
    dashboardInfo: GameDashboardInfo?,
    endOfRoundInfo: EndOfRoundInfo?,
    showGameOver: Boolean,
    onGameOverAcknowledge: () -> Unit,
    onCardClick: (Card) -> Unit,
    onPickClick: () -> Unit,
    onPassClick: () -> Unit,
    connectionStatus: GuestCommunicationState?,
    onReconnectClick: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .animateContentSize()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        if (showGameOver) {
            GameOverDialog(onGameOverAcknowledge)
        }

        if (endOfRoundInfo != null) {
            EndOfRoundScreen(endOfRoundInfo = endOfRoundInfo)
        } else {
            DashboardScreen(
                dashboardInfo = dashboardInfo,
                onCardClick = onCardClick,
                onPickClick = onPickClick,
                onPassClick = onPassClick
            )
        }

        Spacer(Modifier.height(16.dp))
        ConnectionGuestScreen(connectionStatus) { onReconnectClick() }
    }
}

