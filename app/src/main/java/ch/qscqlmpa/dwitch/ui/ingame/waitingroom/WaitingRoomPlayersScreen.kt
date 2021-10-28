package ch.qscqlmpa.dwitch.ui.ingame.waitingroom

import androidx.compose.animation.*
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.TweenSpec
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
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
            ),
            showAddComputerPlayer = true,
            onAddComputerPlayer = {},
            onKickPlayer = {}
        )
    }
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun WaitingRoomPlayers(
    players: List<PlayerWrUi>,
    showAddComputerPlayer: Boolean = false,
    onAddComputerPlayer: () -> Unit = {},
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
            if (showAddComputerPlayer) {
                item {
                    Card(elevation = 0.dp) {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            TextButton(
                                onClick = onAddComputerPlayer,
                                modifier = Modifier.align(Alignment.CenterHorizontally)
                            ) {
                                Icon(
                                    Icons.Filled.AddCircle,
                                    contentDescription = null
                                )
                                Spacer(Modifier.size(ButtonDefaults.IconSpacing))

                                Text(
                                    text = stringResource(R.string.add_computer_player),
                                    fontSize = 20.sp,
                                    modifier = Modifier.testTag(UiTags.addComputerPlayer)
                                )
                            }
                        }
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun PlayerDetailsRow2(player: PlayerWrUi) {
    Row(modifier = Modifier.fillMaxWidth()) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .wrapContentWidth(Alignment.Start)
        ) {
            ReadyState(player.ready)
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

@OptIn(ExperimentalAnimationApi::class)
@Composable
private fun ReadyState(ready: Boolean) {
    val durationMillis = 300
    AnimatedContent(
        targetState = ready,
        transitionSpec = {
            if (targetState) {
                slideInVertically(
                    initialOffsetY = { height -> height },
                    animationSpec = TweenSpec(durationMillis = durationMillis, easing = LinearEasing)
                ) + fadeIn(
                    animationSpec = TweenSpec(durationMillis = durationMillis, easing = LinearEasing)
                ) with slideOutVertically(
                    targetOffsetY = { height -> -height },
                    animationSpec = TweenSpec(durationMillis = durationMillis, easing = LinearEasing)
                ) + fadeOut(animationSpec = TweenSpec(durationMillis = durationMillis, easing = LinearEasing))
            } else {
                slideInVertically(
                    initialOffsetY = { height -> -height },
                    animationSpec = TweenSpec(durationMillis = durationMillis, easing = LinearEasing)
                ) + fadeIn(
                    animationSpec = TweenSpec(durationMillis = durationMillis, easing = LinearEasing)
                ) with slideOutVertically(
                    targetOffsetY = { height -> height },
                    animationSpec = TweenSpec(durationMillis = durationMillis, easing = LinearEasing)
                ) + fadeOut(animationSpec = TweenSpec(durationMillis = durationMillis, easing = LinearEasing))
            }.using(SizeTransform(clip = false))
        }
    ) { targetState ->
        val readyLabel = if (targetState) R.string.ready else R.string.not_ready
        val readyIcon = if (targetState) R.drawable.ic_baseline_check_circle_outline_24 else R.drawable.ic_baseline_clear_24
        val iconTint = if (targetState) Color(0xFF1CE91C) else Color(0xFFFF0000)
        Row {
            Icon(
                painter = painterResource(readyIcon),
                contentDescription = null,
                tint = iconTint
            )
            Spacer(Modifier.width(8.dp))
            Text(stringResource(readyLabel))
        }
    }
}
