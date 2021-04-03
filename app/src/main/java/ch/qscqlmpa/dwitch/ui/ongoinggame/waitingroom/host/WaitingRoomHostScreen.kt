package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.host

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.CommunicationHostScreen
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.WaitingRoomPlayersScreen
import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicationState
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import ch.qscqlmpa.dwitchmodel.player.PlayerWr

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun WaitingRoomHostScreenPreview() {
    WaitingRoomHostScreen(
        players = listOf(
            PlayerWr(PlayerDwitchId(1), "Aragorn", PlayerRole.HOST, PlayerConnectionState.CONNECTED, true),
            PlayerWr(PlayerDwitchId(2), "Boromir", PlayerRole.GUEST, PlayerConnectionState.CONNECTED, false),
            PlayerWr(PlayerDwitchId(3), "Gimli", PlayerRole.GUEST, PlayerConnectionState.DISCONNECTED, false)
        ),
        launchGameEnabled = false,
        connectionStatus = HostCommunicationState.Error,
        onLaunchGameClick = {},
        onCancelGameClick = {},
        onReconnectClick = {}
    )
}

@Composable
fun WaitingRoomHostScreen(
    players: List<PlayerWr>,
    launchGameEnabled: Boolean,
    connectionStatus: HostCommunicationState?,
    onLaunchGameClick: () -> Unit,
    onCancelGameClick: () -> Unit,
    onReconnectClick: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .animateContentSize()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {

        WaitingRoomPlayersScreen(players = players)

        Spacer(modifier = Modifier.height(16.dp))

        HostControlScreen(
            launchGameEnabled = launchGameEnabled,
            onLaunchGameClick = onLaunchGameClick,
            onCancelGameClick = onCancelGameClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        CommunicationHostScreen(connectionStatus) { onReconnectClick() }
    }
}

@Composable
private fun HostControlScreen(
    launchGameEnabled: Boolean,
    onLaunchGameClick: () -> Unit,
    onCancelGameClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = onLaunchGameClick,
            enabled = launchGameEnabled,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                stringResource(id = R.string.wrhf_launch_game_tv),
                color = Color.White
            )
        }
        Spacer(modifier = Modifier.height(16.dp))
        OutlinedButton(
            onClick = onCancelGameClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                stringResource(id = R.string.wrhf_cancel_game_tv),
                color = MaterialTheme.colors.primary
            )
        }
    }
}