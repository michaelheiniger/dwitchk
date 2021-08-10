package ch.qscqlmpa.dwitch.ui.ingame.waitingroom.guest

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.base.ActivityScreenContainer
import ch.qscqlmpa.dwitch.ui.common.*
import ch.qscqlmpa.dwitch.ui.ingame.connection.guest.ConnectionGuestViewModel
import ch.qscqlmpa.dwitch.ui.ingame.waitingroom.WaitingRoomPlayersScreen
import ch.qscqlmpa.dwitch.ui.ingame.waitingroom.WaitingRoomViewModel
import ch.qscqlmpa.dwitch.ui.model.UiCheckboxModel
import ch.qscqlmpa.dwitch.ui.viewmodel.ViewModelFactory
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationState
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.PlayerWrUi

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun WaitingRoomGuestScreenPlayerConnectedPreview() {
    ActivityScreenContainer {
        WaitingRoomGuestBody(
            toolbarTitle = "Dwiiitch",
            players = listOf(
                PlayerWrUi(1L, name = "Aragorn", connected = true, ready = true, kickable = false),
                PlayerWrUi(2L, name = "Boromir", connected = true, ready = false, kickable = false),
                PlayerWrUi(3L, name = "Gimli", connected = false, ready = false, kickable = false)
            ),
            ready = UiCheckboxModel(enabled = false, checked = false),
            notification = WaitingRoomGuestNotification.None,
            connectionStatus = GuestCommunicationState.Connected,
            onReadyClick = {},
            onLeaveConfirmClick = {},
            onReconnectClick = {},
            onGameCanceledAcknowledge = {},
            onKickOffGameAcknowledge = {}
        )
    }
}

@Composable
fun WaitingRoomGuestScreen(
    vmFactory: ViewModelFactory,
    onNavigationEvent: (WaitingRoomGuestDestination) -> Unit
) {
    val viewModel = viewModel<WaitingRoomViewModel>(factory = vmFactory)
    val guestViewModel = viewModel<WaitingRoomGuestViewModel>(factory = vmFactory)
    val connectionViewModel = viewModel<ConnectionGuestViewModel>(factory = vmFactory)

    DisposableEffect(viewModel, guestViewModel, connectionViewModel) {
        viewModel.onStart()
        guestViewModel.onStart()
        connectionViewModel.onStart()
        onDispose {
            viewModel.onStop()
            guestViewModel.onStop()
            connectionViewModel.onStop()
        }
    }

    Navigation(guestViewModel.navigation.value, onNavigationEvent = onNavigationEvent)

    WaitingRoomGuestBody(
        toolbarTitle = viewModel.toolbarTitle.value,
        players = viewModel.players.value,
        ready = guestViewModel.ready.value,
        notification = guestViewModel.notifications.value,
        connectionStatus = connectionViewModel.connectionStatus.value,
        onReadyClick = guestViewModel::updateReadyState,
        onLeaveConfirmClick = guestViewModel::leaveGame,
        onReconnectClick = connectionViewModel::reconnect,
        onGameCanceledAcknowledge = guestViewModel::acknowledgeGameCanceledEvent,
        onKickOffGameAcknowledge = guestViewModel::acknowledgeKickOffGame
    )
}

@Composable
fun Navigation(destination: WaitingRoomGuestDestination, onNavigationEvent: (WaitingRoomGuestDestination) -> Unit) {
    if (destination != WaitingRoomGuestDestination.CurrentScreen) onNavigationEvent(destination)
}

@Composable
fun WaitingRoomGuestBody(
    toolbarTitle: String,
    players: List<PlayerWrUi>,
    ready: UiCheckboxModel,
    notification: WaitingRoomGuestNotification,
    connectionStatus: GuestCommunicationState?,
    onReadyClick: (Boolean) -> Unit,
    onLeaveConfirmClick: () -> Unit,
    onReconnectClick: () -> Unit,
    onGameCanceledAcknowledge: () -> Unit,
    onKickOffGameAcknowledge: () -> Unit
) {
    val showConfirmationDialog = remember { mutableStateOf(false) }

    Column(
        Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        DwitchTopBar(
            title = toolbarTitle,
            navigationIcon = NavigationIcon(
                icon = R.drawable.ic_baseline_exit_to_app_24,
                contentDescription = R.string.leave_game,
                onClick = { showConfirmationDialog.value = true }
            ),
            actions = emptyList(),
            onActionClick = {}
        )
        Column(
            Modifier
                .fillMaxWidth()
                .animateContentSize()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            WaitingRoomPlayersScreen(players = players, showAddComputerPlayer = false, onAddComputerPlayer = {})
            Spacer(Modifier.height(16.dp))
            GuestControlScreen(
                ready = ready,
                onReadyClick = onReadyClick
            )
        }
    }

    when (notification) {
        WaitingRoomGuestNotification.NotifyGameCanceled -> {
            InfoDialog(
                title = R.string.info_dialog_title,
                text = R.string.game_canceled_by_host,
                onOkClick = onGameCanceledAcknowledge
            )
        }
        WaitingRoomGuestNotification.NotifyPlayerKickedOffGame -> {
            InfoDialog(
                title = R.string.info_dialog_title,
                text = R.string.you_have_been_kick,
                onOkClick = onKickOffGameAcknowledge
            )
        }
        WaitingRoomGuestNotification.None -> {
            ConnectionGuestScreen(
                status = connectionStatus,
                onReconnectClick = onReconnectClick,
                onAbortClick = { showConfirmationDialog.value = true }
            )
        }
    }
    if (showConfirmationDialog.value) {
        ConfirmationDialog(
            title = R.string.info_dialog_title,
            text = R.string.guest_leaves_game_confirmation,
            onConfirmClick = onLeaveConfirmClick,
            onCancelClick = { showConfirmationDialog.value = false }
        )
    }
}

@Composable
private fun GuestControlScreen(
    ready: UiCheckboxModel,
    onReadyClick: (Boolean) -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onReadyClick(!ready.checked) }
                .semantics(mergeDescendants = true, properties = {}),
            horizontalArrangement = Arrangement.End
        ) {
            Switch(
                checked = ready.checked,
                enabled = true,
                onCheckedChange = onReadyClick,
                modifier = Modifier.testTag(UiTags.localPlayerReadyControl)
            )
            val label = if (ready.checked) R.string.ready else R.string.not_ready
            val contentDescription = stringResource(getReadyContentDescription(ready))
            Text(
                text = stringResource(label),
                modifier = Modifier
                    .testTag(UiTags.localPlayerReadyText)
                    .semantics { this.contentDescription = contentDescription }
            )
        }
    }
}

private fun getReadyContentDescription(ready: UiCheckboxModel): Int {
    return if (ready.checked) R.string.notify_host_you_are_not_ready_cd else R.string.notify_host_you_are_ready_cd
}
