package ch.qscqlmpa.dwitch.ui.ingame.waitingroom

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.base.PreviewContainer
import ch.qscqlmpa.dwitch.ui.common.UiTags
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.PlayerWrUi

@Preview
@Composable
private fun WaitingRoomPlayersPreview() {
    PreviewContainer {
        WaitingRoomPlayers(
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

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WaitingRoomPlayers(
    players: List<PlayerWrUi>,
    onKickPlayer: (PlayerWrUi) -> Unit = {}
) {
    Column(Modifier.fillMaxWidth()) {
        Text(
            stringResource(R.string.players_in_waitingroom),
            fontSize = 32.sp,
            color = MaterialTheme.colors.primary,
        )
        Spacer(Modifier.height(8.dp))
        LazyColumn(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(players, key = { p -> p.id }) { player ->
                if (player.kickable) {
                    val dismissState = rememberDismissState()
                    if (dismissState.isDismissed(DismissDirection.EndToStart)) {
                        onKickPlayer(player)
                    }
                    SwipeToDismiss(
                        state = dismissState,
                        modifier = Modifier.testTag("${UiTags.kickPlayer}-${player.name}"),
                        directions = setOf(DismissDirection.EndToStart),
                        dismissThresholds = { FractionalThreshold(0.5f) },
                        background = {
                            dismissState.dismissDirection ?: return@SwipeToDismiss
                            val color by animateColorAsState(
                                when (dismissState.targetValue) {
                                    DismissValue.Default -> Color.LightGray
                                    DismissValue.DismissedToEnd -> Color.Green
                                    DismissValue.DismissedToStart -> Color.Red
                                }
                            )
                            val scale by animateFloatAsState(
                                if (dismissState.targetValue == DismissValue.Default) 0.75f else 1f
                            )

                            Row(
                                horizontalArrangement = Arrangement.End,
                                verticalAlignment = Alignment.CenterVertically,
                                modifier = Modifier.fillMaxSize().background(color).padding(horizontal = 20.dp)
                            ) {
                                Text(
                                    text = stringResource(R.string.kick_player, player.name),
                                    color = Color.White,
                                    fontSize = 20.sp,
                                    modifier = Modifier.scale(scale)
                                )
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    tint = Color.White,
                                    contentDescription = stringResource(R.string.kick_player),
                                    modifier = Modifier.scale(scale)
                                )
                            }
                        },
                        dismissContent = {
                            Card(
                                elevation = animateDpAsState(if (dismissState.dismissDirection != null) 4.dp else 0.dp).value,
//                                modifier = Modifier.testTag("${UiTags.kickPlayer}-${player.name}")
                            ) {
                                PlayerListItem(player = player)
                            }
                        }
                    )
                } else {
                    Card(elevation = 0.dp) {
                        PlayerListItem(player = player)
                    }
                }
            }
        }
    }
}

@Composable
private fun PlayerListItem(player: PlayerWrUi) {
    Column(
        Modifier
            .fillMaxWidth()
            .padding(horizontal = 10.dp, vertical = 2.dp)
            .testTag(player.name)
            .semantics(mergeDescendants = true, properties = {}),
    ) {
        PlayerDetailsRow1(player)
        PlayerDetailsRow2(player)
    }
}

@Composable
private fun PlayerDetailsRow1(player: PlayerWrUi) {
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
