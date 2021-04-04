package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.host

import DashboardScreen
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.ConnectionHostScreen
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicationState
import ch.qscqlmpa.dwitchgame.ongoinggame.game.GameDashboardInfo

@Composable
fun GameRoomHostScreen(
    dashboardInfo: GameDashboardInfo?,
    onCardClick: (Card) -> Unit,
    onPickClick: () -> Unit,
    onPassClick: () -> Unit,
    connectionStatus: HostCommunicationState?,
    onEndGameClick: () -> Unit,
    onReconnectClick: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .animateContentSize()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        DashboardScreen(
            dashboardInfo = dashboardInfo,
            onCardClick = onCardClick,
            onPickClick = onPickClick,
            onPassClick = onPassClick
        )

        Spacer(Modifier.height(16.dp))

        OutlinedButton(
            onClick = onEndGameClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(stringResource(R.string.end_game))
        }
        ConnectionHostScreen(connectionStatus) { onReconnectClick() }
    }
}