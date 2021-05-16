package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.guest

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.ConnectionGuestScreen
import ch.qscqlmpa.dwitch.ui.common.DwitchTopBar
import ch.qscqlmpa.dwitch.ui.common.NavigationIcon
import ch.qscqlmpa.dwitch.ui.common.UiTags
import ch.qscqlmpa.dwitch.ui.model.UiCheckboxModel
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.WaitingRoomPlayersScreen
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicationState
import ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom.PlayerWrUi
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun WaitingRoomGuestScreenPlayerConnectedPreview() {
    WaitingRoomGuestScreen(
        toolbarTitle = "Dwiiitch",
        players = listOf(
            PlayerWrUi(1L, name = "Aragorn", PlayerConnectionState.CONNECTED, ready = true, kickable = false),
            PlayerWrUi(2L, name = "Boromir", PlayerConnectionState.CONNECTED, ready = false, kickable = false),
            PlayerWrUi(3L, name = "Gimli", PlayerConnectionState.DISCONNECTED, ready = false, kickable = false)
        ),
        ready = UiCheckboxModel(enabled = false, checked = false),
        connectionStatus = GuestCommunicationState.Connected,
        onReadyClick = {},
        onLeaveClick = {},
        onReconnectClick = {}
    )
}

@Composable
fun WaitingRoomGuestScreen(
    toolbarTitle: String,
    players: List<PlayerWrUi>,
    ready: UiCheckboxModel,
    connectionStatus: GuestCommunicationState?,
    onReadyClick: (Boolean) -> Unit,
    onLeaveClick: () -> Unit,
    onReconnectClick: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        DwitchTopBar(
            title = toolbarTitle,
            navigationIcon = NavigationIcon(R.drawable.ic_baseline_exit_to_app_24, R.string.leave_game, onLeaveClick)
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
            Spacer(Modifier.height(16.dp))
            ConnectionGuestScreen(
                status = connectionStatus,
                onReconnectClick = onReconnectClick,
                onAbortClick = onLeaveClick
            )
        }
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
