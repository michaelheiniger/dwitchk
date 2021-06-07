package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.host

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.base.ActivityScreenContainer
import ch.qscqlmpa.dwitch.ui.common.*
import ch.qscqlmpa.dwitch.ui.ongoinggame.connection.host.ConnectionHostViewModel
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.WaitingRoomPlayersScreen
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.WaitingRoomViewModel
import ch.qscqlmpa.dwitch.ui.viewmodel.ViewModelFactory
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicationState
import ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom.PlayerWrUi

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun WaitingRoomHostScreenPreview() {
    ActivityScreenContainer {
        WaitingRoomHostBody(
            toolbarTitle = "Dwiiitch",
            showAddComputerPlayer = true,
            players = listOf(
                PlayerWrUi(1L, "Aragorn", connected = true, ready = true, kickable = false),
                PlayerWrUi(2L, "Boromir", connected = true, ready = false, kickable = true),
                PlayerWrUi(3L, "Gimli", connected = false, ready = false, kickable = true)
            ),
            launchGameEnabled = false,
            connectionStatus = HostCommunicationState.Error,
            onLaunchGameClick = {},
            onCancelGameClick = {},
            onReconnectClick = {}
        )
    }
}

@Composable
fun WaitingRoomHostScreen(
    vmFactory: ViewModelFactory,
    onNavigationEvent: (WaitingRoomHostDestination) -> Unit
) {
    val viewModel = viewModel<WaitingRoomViewModel>(factory = vmFactory)
    val hostViewModel = viewModel<WaitingRoomHostViewModel>(factory = vmFactory)
    val connectionViewModel = viewModel<ConnectionHostViewModel>(factory = vmFactory)

    DisposableEffect(viewModel, hostViewModel, connectionViewModel) {
        viewModel.onStart()
        hostViewModel.onStart()
        connectionViewModel.onStart()
        onDispose {
            viewModel.onStop()
            hostViewModel.onStop()
            connectionViewModel.onStop()
        }
    }

    val event = hostViewModel.navigation.observeAsState().value
    if (event != null) onNavigationEvent(event)

    val showConfirmationDialog = remember { mutableStateOf(false) }

    val toolbarTitle = viewModel.toolbarTitle.observeAsState(toolbarDefaultTitle).value
    val showAddComputerPlayer = viewModel.canComputerPlayersBeAdded.observeAsState(false).value
    val players = viewModel.players.observeAsState(emptyList()).value
    val launchGameEnabled = hostViewModel.canGameBeLaunched.observeAsState(false).value
    val connectionStatus = connectionViewModel.connectionStatus.observeAsState().value
    WaitingRoomHostBody(
        toolbarTitle = toolbarTitle,
        showAddComputerPlayer = showAddComputerPlayer,
        players = players,
        launchGameEnabled = launchGameEnabled,
        connectionStatus = connectionStatus,
        onLaunchGameClick = hostViewModel::launchGame,
        onCancelGameClick = { showConfirmationDialog.value = true },
        onReconnectClick = connectionViewModel::reconnect,
        onAddComputerPlayer = hostViewModel::addComputerPlayer,
        onKickPlayer = hostViewModel::kickPlayer
    )
    if (showConfirmationDialog.value) {
        ConfirmationDialog(
            title = R.string.info_dialog_title,
            text = R.string.host_cancel_game_confirmation,
            onConfirmClick = { hostViewModel.cancelGame() },
            onCancelClick = { showConfirmationDialog.value = false }
        )
    }
    if (hostViewModel.loading.observeAsState(false).value) LoadingDialog()
}

@Composable
fun WaitingRoomHostBody(
    toolbarTitle: String,
    showAddComputerPlayer: Boolean,
    players: List<PlayerWrUi>,
    launchGameEnabled: Boolean,
    connectionStatus: HostCommunicationState?,
    onLaunchGameClick: () -> Unit,
    onCancelGameClick: () -> Unit,
    onReconnectClick: () -> Unit,
    onAddComputerPlayer: () -> Unit = {},
    onKickPlayer: (PlayerWrUi) -> Unit = {}
) {
    Column(
        Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        DwitchTopBar(
            title = toolbarTitle,
            navigationIcon = NavigationIcon(R.drawable.ic_baseline_exit_to_app_24, R.string.cancel_game, onCancelGameClick),
            actions = emptyList(),
            onActionClick = {}
        )
        Column(
            Modifier
                .fillMaxWidth()
                .animateContentSize()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            WaitingRoomPlayersScreen(
                players = players,
                showAddComputerPlayer = showAddComputerPlayer,
                onAddComputerPlayer = onAddComputerPlayer,
                onKickPlayer = onKickPlayer
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
