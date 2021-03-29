package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Checkbox
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import ch.qscqlmpa.dwitchmodel.player.PlayerWr

@Preview
@Composable
private fun WaitingRoomPlayersScreenPreview() {
    WaitingRoomPlayersScreen(
        players = listOf(
            PlayerWr(
                dwitchId = PlayerDwitchId(1),
                name = "Mirlick",
                playerRole = PlayerRole.HOST,
                connectionState = PlayerConnectionState.CONNECTED,
                ready = true
            ),
            PlayerWr(
                dwitchId = PlayerDwitchId(2),
                name = "Mébène",
                playerRole = PlayerRole.GUEST,
                connectionState = PlayerConnectionState.DISCONNECTED,
                ready = false
            )
        )
    )
}

@Composable
fun WaitingRoomPlayersScreen(players: List<PlayerWr>) {
    Column(modifier = Modifier.fillMaxWidth()) {
        Text(
            stringResource(id = R.string.wra_player_list),
            fontSize = 32.sp,
            color = MaterialTheme.colors.primary
        )
        Spacer(modifier = Modifier.height(8.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(players) { player ->
                Column(modifier = Modifier.fillMaxWidth()) {
                    Text(
                        text = player.name,
                        fontSize = 24.sp,
                        color = MaterialTheme.colors.secondary
                    )
                    PlayerStateDetails(player)
                }
            }
        }
    }
}

@Composable
private fun PlayerStateDetails(player: PlayerWr) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .wrapContentWidth(Alignment.Start)
        ) {
            Checkbox(
                checked = player.ready,
                enabled = false,
                onCheckedChange = {}
            )
            val readyLabel = if (player.ready) R.string.ready else R.string.not_ready
            Text(stringResource(readyLabel))
        }

        val connectionLabel = when (player.connectionState) {
            PlayerConnectionState.CONNECTED -> R.string.player_connected
            PlayerConnectionState.DISCONNECTED -> R.string.player_disconnected
        }
        Text(
            stringResource(connectionLabel),
            modifier = Modifier
                .weight(1f)
                .wrapContentWidth(Alignment.End)
        )
    }
}