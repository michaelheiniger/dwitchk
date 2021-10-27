package ch.qscqlmpa.dwitch.ui.ingame.waitingroom.guest

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Scaffold
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
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.base.PreviewContainer
import ch.qscqlmpa.dwitch.ui.common.*
import ch.qscqlmpa.dwitch.ui.ingame.connection.guest.ConnectionGuestViewModel
import ch.qscqlmpa.dwitch.ui.ingame.waitingroom.WaitingRoomPlayers
import ch.qscqlmpa.dwitch.ui.ingame.waitingroom.WaitingRoomViewModel
import ch.qscqlmpa.dwitch.ui.model.UiCheckboxModel
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationState
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.PlayerWrUi

@Preview
@Composable
private fun WaitingRoomGuestBodyPlayerConnectedPreview() {
    PreviewContainer {
        WaitingRoomGuestBody(
            toolbarTitle = "Dwiiitch",
            players = listOf(
                PlayerWrUi(1L, name = "Aragorn", connected = true, ready = true, kickable = false),
                PlayerWrUi(2L, name = "Boromir", connected = true, ready = false, kickable = false),
                PlayerWrUi(3L, name = "Gimli", connected = false, ready = false, kickable = false)
            ),
            ready = UiCheckboxModel(enabled = false, checked = false),
            notification = WaitingRoomGuestNotification.None,
            connectionState = GuestCommunicationState.Connected,
            leavingGame = false,
            onReadyClick = {},
            onLeaveConfirmClick = {},
            onReconnectClick = {},
            onGameCanceledAcknowledge = {},
            onKickOffGameAcknowledge = {}
        )
    }
}

@Composable
fun WaitingRoomGuestBody(
    waitingRoomViewModel: WaitingRoomViewModel,
    guestViewModel: WaitingRoomGuestViewModel,
    connectionViewModel: ConnectionGuestViewModel
) {
    DisposableEffect(waitingRoomViewModel, guestViewModel, connectionViewModel) {
        waitingRoomViewModel.onStart()
        guestViewModel.onStart()
        connectionViewModel.onStart()
        onDispose {
            waitingRoomViewModel.onStop()
            guestViewModel.onStop()
            connectionViewModel.onStop()
        }
    }

    WaitingRoomGuestBody(
        toolbarTitle = waitingRoomViewModel.toolbarTitle.value,
        players = waitingRoomViewModel.players.value,
        ready = guestViewModel.ready.value,
        notification = guestViewModel.notifications.value,
        connectionState = connectionViewModel.connectionState.value,
        leavingGame = guestViewModel.leavingGame.value,
        onReadyClick = guestViewModel::updateReadyState,
        onLeaveConfirmClick = guestViewModel::leaveGame,
        onReconnectClick = connectionViewModel::reconnect,
        onGameCanceledAcknowledge = guestViewModel::acknowledgeGameCanceled,
        onKickOffGameAcknowledge = guestViewModel::acknowledgeKickOffGame
    )
}

@Composable
fun WaitingRoomGuestBody(
    toolbarTitle: String,
    players: List<PlayerWrUi>,
    ready: UiCheckboxModel,
    notification: WaitingRoomGuestNotification,
    connectionState: GuestCommunicationState,
    leavingGame: Boolean,
    onReadyClick: (Boolean) -> Unit,
    onLeaveConfirmClick: () -> Unit,
    onReconnectClick: () -> Unit,
    onGameCanceledAcknowledge: () -> Unit,
    onKickOffGameAcknowledge: () -> Unit
) {
    val showLeaveGameConfirmationDialog = remember { mutableStateOf(false) }
    Scaffold(
        topBar = {
            DwitchTopBar(
                title = toolbarTitle,
                navigationIcon = NavigationIcon(
                    icon = R.drawable.ic_baseline_exit_to_app_24,
                    contentDescription = R.string.leave_game,
                    onClick = { showLeaveGameConfirmationDialog.value = true }
                )
            )
        }
    ) {
        Column(
            Modifier
                .fillMaxWidth()
                .animateContentSize()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp, bottom = 16.dp)
        ) {
            WaitingRoomPlayers(players = players)
            Spacer(Modifier.height(16.dp))
            GuestControls(
                ready = ready,
                onReadyClick = onReadyClick
            )
        }
    }

    when (notification) {
        WaitingRoomGuestNotification.NotifyGameCanceled -> {
            InfoDialog(
                title = R.string.dialog_info_title,
                text = R.string.game_canceled_by_host,
                onOkClick = onGameCanceledAcknowledge
            )
        }
        WaitingRoomGuestNotification.NotifyPlayerKickedOffGame -> {
            InfoDialog(
                title = R.string.dialog_info_title,
                text = R.string.you_have_been_kick,
                onOkClick = onKickOffGameAcknowledge
            )
        }
        WaitingRoomGuestNotification.None -> {
            CommunicationGuest(
                state = connectionState,
                onReconnectClick = onReconnectClick,
                onAbortClick = { showLeaveGameConfirmationDialog.value = true }
            )
        }
    }
    if (leavingGame) LoadingDialog(R.string.leaving_game)
    if (showLeaveGameConfirmationDialog.value) {
        ConfirmationDialog(
            title = R.string.dialog_info_title,
            text = R.string.guest_leaves_game_confirmation,
            onConfirmClick = onLeaveConfirmClick,
            onClosing = { showLeaveGameConfirmationDialog.value = false }
        )
    }
}

@Composable
private fun GuestControls(
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
