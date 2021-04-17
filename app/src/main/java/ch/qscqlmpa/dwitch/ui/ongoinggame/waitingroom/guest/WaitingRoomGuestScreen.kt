package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.guest

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Switch
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.ConnectionGuestScreen
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
private fun WaitingRoomGuestScreenPreview() {
    WaitingRoomGuestScreen(
        players = listOf(
            PlayerWrUi(name = "Aragorn", PlayerConnectionState.CONNECTED, ready = true),
            PlayerWrUi(name = "Boromir", PlayerConnectionState.CONNECTED, ready = false),
            PlayerWrUi(name = "Gimli", PlayerConnectionState.DISCONNECTED, ready = false)
        ),
        ready = UiCheckboxModel(enabled = false, checked = false),
        connectionStatus = GuestCommunicationState.Disconnected,
        onReadyClick = {},
        onLeaveClick = {},
        onReconnectClick = {}
    )
}

@Composable
fun WaitingRoomGuestScreen(
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
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)
    ) {
        WaitingRoomPlayersScreen(players)

        Spacer(Modifier.height(16.dp))

        GuestControlScreen(
            ready = ready,
            onReadyClick = onReadyClick,
            onLeaveClick = onLeaveClick
        )

        Spacer(Modifier.height(16.dp))

        ConnectionGuestScreen(connectionStatus) { onReconnectClick() }
    }
}

@Composable
private fun GuestControlScreen(
    ready: UiCheckboxModel,
    onReadyClick: (Boolean) -> Unit,
    onLeaveClick: () -> Unit
) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            Switch(
                checked = ready.checked,
                enabled = true,
                onCheckedChange = onReadyClick,
                modifier = Modifier.testTag(UiTags.localPlayerReadyCheckbox)
            )
            val label = if (ready.checked) R.string.ready else R.string.not_ready
            Text(
                text = stringResource(label),
                modifier = Modifier
                    .clickable { onReadyClick(!ready.checked) }
                    .testTag(UiTags.localPlayerReadyText)
            )
        }

        Spacer(Modifier.height(16.dp))

        OutlinedButton(
            onClick = onLeaveClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                text = stringResource(R.string.leave_game_btn),
                color = MaterialTheme.colors.primary
            )
        }
    }
}
