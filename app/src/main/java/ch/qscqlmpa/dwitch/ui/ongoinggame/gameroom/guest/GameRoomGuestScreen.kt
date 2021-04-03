package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.guest

import DashboardScreen
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import ch.qscqlmpa.dwitch.ui.CommunicationGuestScreen
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicationState
import ch.qscqlmpa.dwitchgame.ongoinggame.game.GameDashboardInfo

@Composable
fun GameRoomGuestScreen(
    dashboardInfo: GameDashboardInfo?,
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
        DashboardScreen(
            dashboardInfo = dashboardInfo,
            onCardClick = onCardClick,
            onPickClick = onPickClick,
            onPassClick = onPassClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        CommunicationGuestScreen(connectionStatus) { onReconnectClick() }
    }
}

