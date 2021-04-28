package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.host

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.ConnectionHostScreen
import ch.qscqlmpa.dwitch.ui.common.DwitchTopBar
import ch.qscqlmpa.dwitch.ui.common.NavigationIcon
import ch.qscqlmpa.dwitch.ui.common.UiTags
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
        toolbarTitle = "Dwiiitch",
        showAddComputerPlayer = true,
        players = listOf(
            PlayerWrUi("Aragorn", PlayerConnectionState.CONNECTED, true),
            PlayerWrUi("Boromir", PlayerConnectionState.CONNECTED, false),
            PlayerWrUi("Gimli", PlayerConnectionState.DISCONNECTED, false)
        ),
        launchGameEnabled = false,
        connectionStatus = HostCommunicationState.Error,
        onAddComputerPlayer = {},
        onLaunchGameClick = {},
        onCancelGameClick = {},
        onReconnectClick = {}
    )
}

@Composable
fun WaitingRoomHostScreen(
    toolbarTitle: String,
    showAddComputerPlayer: Boolean,
    players: List<PlayerWrUi>,
    launchGameEnabled: Boolean,
    connectionStatus: HostCommunicationState?,
    onAddComputerPlayer: () -> Unit,
    onLaunchGameClick: () -> Unit,
    onCancelGameClick: () -> Unit,
    onReconnectClick: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        DwitchTopBar(
            title = toolbarTitle,
            navigationIcon = NavigationIcon(R.drawable.ic_baseline_exit_to_app_24, R.string.end_game, onCancelGameClick)
        )
        Column(
            Modifier
                .fillMaxWidth()
                .animateContentSize()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            WaitingRoomPlayersScreen(
                players = players,
                showAddComputerPlayer = showAddComputerPlayer,
                onAddComputerPlayer = onAddComputerPlayer
            )
            Spacer(Modifier.height(16.dp))
            HostControlScreen(
                launchGameEnabled = launchGameEnabled,
                onLaunchGameClick = onLaunchGameClick
            )
            Spacer(Modifier.height(16.dp))
            ConnectionHostScreen(
                status = connectionStatus,
                onReconnectClick = onReconnectClick,
                onAbortClick = onCancelGameClick
            )
        }
    }
}

@Composable
private fun HostControlScreen(
    launchGameEnabled: Boolean,
    onLaunchGameClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Button(
            onClick = onLaunchGameClick,
            enabled = launchGameEnabled,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(UiTags.launchGameControl)
        ) {
            Text(stringResource(R.string.launch_game), color = Color.White)
        }
    }
}
