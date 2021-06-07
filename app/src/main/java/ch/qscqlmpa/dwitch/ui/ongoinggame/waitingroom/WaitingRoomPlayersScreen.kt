package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.base.ActivityScreenContainer
import ch.qscqlmpa.dwitch.ui.common.UiTags
import ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom.PlayerWrUi

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun WaitingRoomPlayersScreenPreview() {
    ActivityScreenContainer {
        WaitingRoomPlayersScreen(
            showAddComputerPlayer = true,
            players = listOf(
                PlayerWrUi(
                    id = 1L,
                    name = "Mirlick",
                    connected = true,
                    ready = true,
                    kickable = true
                ),
                PlayerWrUi(
                    id = 2L,
                    name = "Mébène",
                    connected = false,
                    ready = false,
                    kickable = false
                )
            )
        )
    }
}

@Composable
fun WaitingRoomPlayersScreen(
    showAddComputerPlayer: Boolean,
    players: List<PlayerWrUi>,
    onAddComputerPlayer: () -> Unit = {},
    onKickPlayer: (PlayerWrUi) -> Unit = {}
) {
    Column(Modifier.fillMaxWidth()) {
        Row(Modifier.fillMaxWidth()) {
            Text(
                stringResource(R.string.players_in_waitingroom),
                fontSize = 32.sp,
                color = MaterialTheme.colors.primary,
                modifier = Modifier
                    .weight(1f)
                    .wrapContentWidth(Alignment.Start)
            )
            if (showAddComputerPlayer) {
                TextButton(
                    onClick = onAddComputerPlayer,
                    modifier = Modifier
                        .weight(1f)
                        .wrapContentWidth(Alignment.End)
                        .testTag(UiTags.addComputerPlayer)
                ) { Text(stringResource(R.string.add_computer_player)) }
                Spacer(Modifier.height(8.dp))
            }
        }
        Spacer(Modifier.height(8.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(players, key = { p -> p.id }) { player ->
                Column(
                    Modifier
                        .fillMaxWidth()
                        .testTag(player.name)
                        .semantics(mergeDescendants = true, properties = {}),
                ) {
                    PlayerDetailsRow1(player, onKickPlayer = onKickPlayer)
                    PlayerDetailsRow2(player)
                }
            }
        }
    }
}

@Composable
private fun PlayerDetailsRow1(
    player: PlayerWrUi,
    onKickPlayer: (PlayerWrUi) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = player.name,
            fontSize = 24.sp,
            color = MaterialTheme.colors.secondary,
            modifier = Modifier
                .weight(1f)
                .wrapContentWidth(Alignment.Start)
        )
        if (player.kickable) {
            IconButton(
                onClick = { onKickPlayer(player) },
                modifier = Modifier
                    .testTag("${UiTags.kickPlayer}-${player.name}")
                    .weight(1f)
                    .wrapContentWidth(Alignment.End)
            ) {
                Icon(
                    painter = painterResource(R.drawable.ic_baseline_clear_24),
                    contentDescription = stringResource(R.string.kick_player, player.name)
                )
            }
        }
    }
}

@Composable
private fun PlayerDetailsRow2(player: PlayerWrUi) {
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

        val connectionLabel = when (player.connected) {
            true -> R.string.player_connected
            false -> R.string.player_disconnected
        }
        Text(
            stringResource(connectionLabel),
            modifier = Modifier
                .weight(1f)
                .wrapContentWidth(Alignment.End)
        )
    }
}
