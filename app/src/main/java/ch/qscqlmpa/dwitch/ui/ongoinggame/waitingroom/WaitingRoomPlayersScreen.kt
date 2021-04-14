package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom.PlayerWrUi
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun WaitingRoomPlayersScreenPreview() {
    WaitingRoomPlayersScreen(
        players = listOf(
            PlayerWrUi(
                name = "Mirlick",
                connectionState = PlayerConnectionState.CONNECTED,
                ready = true
            ),
            PlayerWrUi(
                name = "Mébène",
                connectionState = PlayerConnectionState.DISCONNECTED,
                ready = false
            )
        )
    )
}

@Composable
fun WaitingRoomPlayersScreen(players: List<PlayerWrUi>) {
    Column(Modifier.fillMaxWidth()) {
        Text(
            stringResource(R.string.players_in_waitingroom),
            fontSize = 32.sp,
            color = MaterialTheme.colors.primary
        )
        Spacer(Modifier.height(8.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(players) { player ->
                Column(
                    Modifier
                        .fillMaxWidth()
                        .testTag(player.name)) {
                    PlayerName(player.name)
                    PlayerStateDetails(player)
                }
            }
        }
    }
}

@Composable
private fun PlayerName(name: String) {
    Text(
        text = name,
        fontSize = 24.sp,
        color = MaterialTheme.colors.secondary
    )
}

@Composable
private fun PlayerStateDetails(player: PlayerWrUi) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .wrapContentWidth(Alignment.Start)
        ) {
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