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
import ch.qscqlmpa.dwitch.ui.ConnectionHostScreen
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.WaitingRoomPlayersScreen
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicationState
import ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom.PlayerWrUi
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun WaitingRoomHostScreenPreview() {
    WaitingRoomHostScreen(
        players = listOf(
            PlayerWrUi("Aragorn", PlayerConnectionState.CONNECTED, true),
            PlayerWrUi("Boromir", PlayerConnectionState.CONNECTED, false),
            PlayerWrUi("Gimli", PlayerConnectionState.DISCONNECTED, false)
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
    players: List<PlayerWrUi>,
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

        Spacer(Modifier.height(16.dp))

        HostControlScreen(
            launchGameEnabled = launchGameEnabled,
            onLaunchGameClick = onLaunchGameClick,
            onCancelGameClick = onCancelGameClick
        )

        Spacer(Modifier.height(16.dp))

        ConnectionHostScreen(connectionStatus) { onReconnectClick() }
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
                stringResource(R.string.launch_game),
                color = Color.White
            )
        }
        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = onCancelGameClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                stringResource(R.string.cancel_game),
                color = MaterialTheme.colors.primary
            )
        }
    }
}
