package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.guest

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.CommunicationGuestScreen
import ch.qscqlmpa.dwitch.ui.model.UiCheckboxModel
import ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.WaitingRoomPlayersScreen
import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicationState
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import ch.qscqlmpa.dwitchmodel.player.PlayerWr

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun WaitingRoomGuestScreenPreview() {
    WaitingRoomGuestScreen(
        players = listOf(
            PlayerWr(PlayerDwitchId(1), "Aragorn", PlayerRole.HOST, PlayerConnectionState.CONNECTED, true),
            PlayerWr(PlayerDwitchId(2), "Boromir", PlayerRole.GUEST, PlayerConnectionState.CONNECTED, false),
            PlayerWr(PlayerDwitchId(3), "Gimli", PlayerRole.GUEST, PlayerConnectionState.DISCONNECTED, false)
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
    players: List<PlayerWr>,
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
        WaitingRoomPlayersScreen(players = players)

        Spacer(modifier = Modifier.height(16.dp))

        GuestControlScreen(
            ready = ready,
            onReadyClick = onReadyClick,
            onLeaveClick = onLeaveClick
        )

        Spacer(modifier = Modifier.height(16.dp))

        CommunicationGuestScreen(connectionStatus) { onReconnectClick() }
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
            Checkbox(
                checked = ready.checked,
                enabled = true,
                onCheckedChange = onReadyClick
            )
            val label = if (ready.checked) R.string.ready else R.string.not_ready
            Text(
                text = stringResource(label),
                modifier = Modifier.clickable { onReadyClick(!ready.checked) }
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        OutlinedButton(
            onClick = onLeaveClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(
                stringResource(R.string.leave_game_btn),
                color = MaterialTheme.colors.primary
            )
        }
    }
}