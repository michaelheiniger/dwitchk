package ch.qscqlmpa.dwitch.ui.ingame.gameroom

import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.qscqlmpa.dwitch.BuildConfig
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.ResourceMapper
import ch.qscqlmpa.dwitch.ui.base.PreviewContainer
import ch.qscqlmpa.dwitch.ui.common.UiTags
import ch.qscqlmpa.dwitch.ui.common.WaitingDialog
import ch.qscqlmpa.dwitch.ui.ingame.PlayerHand
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.PlayedCards
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerStatus
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank
import ch.qscqlmpa.dwitchgame.ingame.gameroom.PlayerAction
import ch.qscqlmpa.dwitchgame.ingame.gameroom.PlayerInfo

@Preview
@Composable
private fun DashboardPreview() {
    val donePlayer = PlayerInfo(
        name = "Aragorn",
        rank = DwitchRank.VicePresident,
        status = DwitchPlayerStatus.Done,
        dwitched = false,
        localPlayer = false
    )

    val turnPassedPlayer = PlayerInfo(
        name = "Gimli",
        rank = DwitchRank.ViceAsshole,
        status = DwitchPlayerStatus.TurnPassed,
        dwitched = false,
        localPlayer = false
    )

    // Also the local player
    val playingPlayer = PlayerInfo(
        name = "Legolas",
        rank = DwitchRank.Asshole,
        status = DwitchPlayerStatus.Playing,
        dwitched = false,
        localPlayer = true
    )

    // Waiting
    val waitingPlayer = PlayerInfo(
        name = "Elrond",
        rank = DwitchRank.President,
        status = DwitchPlayerStatus.Waiting,
        dwitched = false,
        localPlayer = false
    )

    val waitingAndDwitchedPlayer = PlayerInfo(
        name = "Galadriel",
        rank = DwitchRank.Neutral,
        status = DwitchPlayerStatus.Waiting,
        dwitched = true,
        localPlayer = false
    )

    val players = listOf(donePlayer, turnPassedPlayer, playingPlayer, waitingPlayer, waitingAndDwitchedPlayer)

    val localPlayerDashboard = LocalPlayerInfo(
        cardsInHand = listOf(
            CardInfo(Card.Clubs2, selectable = false, selected = false),
            CardInfo(Card.Spades3, selectable = false, selected = false),
            CardInfo(Card.Hearts4, selectable = false, selected = false),
            CardInfo(Card.Clubs4, selectable = false, selected = false),
            CardInfo(Card.Hearts5, selectable = false, selected = false),
            CardInfo(Card.Hearts6, selectable = false, selected = false),
            CardInfo(Card.Diamonds8, selectable = true, selected = true),
            CardInfo(Card.Hearts8, selectable = true, selected = true),
            CardInfo(Card.Diamonds9, selectable = false, selected = false),
            CardInfo(Card.Clubs10, selectable = false, selected = false),
            CardInfo(Card.SpadesJack, selectable = false, selected = false),
            CardInfo(Card.ClubsJack, selectable = false, selected = false),
            CardInfo(Card.HeartsJack, selectable = false, selected = false),
            CardInfo(Card.ClubsAce, selectable = false, selected = false)
        ),
        canPass = true,
        canPlay = true
    )

    val dashboardInfo = DashboardInfo(
        playersInfo = players,
        localPlayerInfo = localPlayerDashboard,
        lastPlayerAction = PlayerAction.PlayCards(
            playerName = "Aragorn",
            playedCards = PlayedCards(listOf(Card.Clubs8, Card.Spades8)),
            clearsTable = false
        ),
        lastCardOnTable = PlayedCards(listOf(Card.Clubs8, Card.Spades8)),
        waitingForPlayerReconnection = false
    )

    PreviewContainer {
        Dashboard(
            dashboardInfo = dashboardInfo,
            onCardClick = {},
            onPlayClick = {},
            onPassClick = {},
            onEndOrLeaveGameClick = {}
        )
    }
}

@Composable
fun Dashboard(
    dashboardInfo: DashboardInfo,
    onCardClick: (Card) -> Unit,
    onPlayClick: () -> Unit,
    onPassClick: () -> Unit,
    onEndOrLeaveGameClick: () -> Unit
) {
    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            Modifier
                .fillMaxSize()
                .animateContentSize()
        ) {
            PlayersInfo(dashboardInfo.playersInfo)
            Spacer(Modifier.height(16.dp))
            Text(text = "Previous move")
            Spacer(Modifier.height(16.dp))
            Table(dashboardInfo.lastCardOnTable, dashboardInfo.lastPlayerAction)
            Spacer(Modifier.height(16.dp))
            Controls(dashboardInfo, onPassClick = onPassClick, onPlayClick = onPlayClick)
            Spacer(Modifier.height(16.dp))
            PlayerHand(dashboardInfo.localPlayerInfo.cardsInHand, onCardClick = onCardClick)
        }
        // FIXME
//        Column(Modifier.align(Alignment.Center)) {
//            ShowLastPlayerAction(dashboardInfo.lastPlayerAction)
//        }
    }

    if (dashboardInfo.waitingForPlayerReconnection) {
        WaitingDialog(
            text = R.string.waiting_for_disconnected_player,
            abortLabel = R.string.leave_game,
            onAbortClick = onEndOrLeaveGameClick
        )
    }
}

@Composable
private fun ShowLastPlayerAction(lastAction: PlayerAction?) {
//    Surface(
//        modifier = Modifier.fillMaxSize(),
//        border = BorderStroke(5.dp, Color.Red)
//    ) {
    Text("Salut")
//    }

    val cards = remember { mutableStateOf(listOf(Card.Clubs8, Card.Hearts8)) }
    val visibleState = remember { mutableStateOf(MutableTransitionState(false)) }
    visibleState.value.targetState = true

//    LaunchedEffect(visibleState.value) {
//        if (visibleState.value.isIdle) {
//            visibleState.value.targetState = false
//        }
//        snapshotFlow {
//            visibleState
//            visibleState.value.isIdle
//            emptyList()
//            model.items.firstOrNull { it.visible.isIdle && !it.visible.targetState }
//        }.collect { value -> value. }
//            if (it != null) {
//                visibleState.value.targetState = false
//                model.pruneItems()
//            }
//        }
//    }

    AnimatedVisibility(
        visibleState = visibleState.value,
        enter = expandIn(tween(durationMillis = 10000))
    ) {
        LazyRow(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(UiTags.lastCardPlayed)
                .semantics(mergeDescendants = true) {}
        ) {
            items(cards.value) { card ->
                Image(
                    painter = painterResource(ResourceMapper.getImageResource(card)),
                    contentDescription = "",
                    modifier = Modifier
                        .fillMaxWidth(0.25f) // Max 4 cards can be played at once (1/4 == 0.25)
                        .testTag(card.toString())
                )
            }
        }
    }

    when (lastAction) {
        is PlayerAction.PassTurn -> {
            // TODO
        }
        is PlayerAction.PlayCards -> {
            if (lastAction.dwitchedPlayedName != null) {
                // TODO
            }
        }
        null -> {
            // Nothing to do
        }
    }
}

@Composable
private fun Controls(
    dashboardInfo: DashboardInfo,
    onPassClick: () -> Unit,
    onPlayClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        AnimatedVisibility(
            visible = dashboardInfo.localPlayerInfo.canPass,
            enter = expandIn(expandFrom = Alignment.Center),
            exit = shrinkOut(shrinkTowards = Alignment.Center),
            modifier = Modifier.clip(CircleShape)
        ) {
            FloatingActionButton(
                backgroundColor = MaterialTheme.colors.primary,
                modifier = Modifier.testTag(UiTags.passTurnControl),
                onClick = onPassClick
            ) { Text(stringResource(R.string.pass_turn)) }
        }
        if (dashboardInfo.localPlayerInfo.canPass) Spacer(Modifier.width(16.dp))
        AnimatedVisibility(
            visible = dashboardInfo.localPlayerInfo.canPlay,
            enter = expandIn(expandFrom = Alignment.Center),
            exit = shrinkOut(shrinkTowards = Alignment.Center),
            modifier = Modifier.clip(CircleShape)
        ) {
            FloatingActionButton(
                backgroundColor = MaterialTheme.colors.primary,
                modifier = Modifier.testTag(UiTags.playCardControl),
                onClick = onPlayClick,
            ) { Text(stringResource(R.string.play_card)) }
        }
    }
}

@Composable
private fun PlayersInfo(playersInfo: List<PlayerInfo>) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .testTag(UiTags.gameRoomPlayersInfo),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (player in playersInfo) {
            when (player.status) {
                DwitchPlayerStatus.Done -> PlayerDone(player)
                DwitchPlayerStatus.Playing -> PlayerPlaying(player)
                DwitchPlayerStatus.TurnPassed -> PlayerTurnPassed(player)
                DwitchPlayerStatus.Waiting -> PlayerWaiting(player)
            }
            Spacer(Modifier.height(4.dp))
        }
    }
}

private sealed class TableState {
    abstract fun cards(): List<Card>

    @Composable
    abstract fun contentDescription(): String

    object Empty : TableState() {
        override fun cards() = listOf(Card.Blank)

        @Composable
        override fun contentDescription(): String {
            return stringResource(R.string.table_is_empty_cd)
        }
    }

    data class Cards(val playedCards: PlayedCards) : TableState() {
        override fun cards() = playedCards.cards

        @Composable
        override fun contentDescription(): String {
            val lastCardPlayedRes = stringResource(ResourceMapper.getContentDescriptionResource(playedCards.name))
            return stringResource(R.string.last_card_played_cd, playedCards.multiplicity, lastCardPlayedRes)
        }
    }
}

@Composable
private fun Table(lastCardPlayed: PlayedCards?, lastAction: PlayerAction?) {

//    if (lastAction != null && lastAction is PlayerAction.PlayCards && lastAction.clearsTable) {
//        // TODO: Show table with cards from lastAction.cards and then clear table (need callback when AnimatedContent animation ends ?)
//    } else {
//        // TODO: Do as currently
//    }

    val tableState = if (lastCardPlayed != null) TableState.Cards(lastCardPlayed) else TableState.Empty
    val durationMillis = 500
    AnimatedContent(
        targetState = tableState,
        transitionSpec = {
            when (initialState) {
                TableState.Empty -> {
                    when (targetState) {
                        TableState.Empty -> {
                            EnterTransition.None with ExitTransition.None
                        }
                        is TableState.Cards -> {
                            slideInHorizontally(
                                initialOffsetX = { width -> width },
                                animationSpec = TweenSpec(durationMillis = durationMillis, easing = LinearEasing)
                            ) + fadeIn(
                                animationSpec = TweenSpec(durationMillis = durationMillis, easing = LinearEasing)
                            ) with fadeOut(
                                animationSpec = TweenSpec(
                                    durationMillis = durationMillis,
                                    easing = FastOutLinearInEasing
                                )
                            )
                        }
                    }
                }
                is TableState.Cards -> {
                    when (targetState) {
                        TableState.Empty -> {
                            fadeIn(
                                animationSpec = TweenSpec(durationMillis = durationMillis, easing = FastOutLinearInEasing)
                            ) with slideOutHorizontally(
                                targetOffsetX = { width -> -width },
                                animationSpec = TweenSpec(durationMillis = durationMillis, easing = LinearEasing)
                            ) + fadeOut(animationSpec = TweenSpec(durationMillis = durationMillis, easing = LinearEasing))
                        }
                        is TableState.Cards -> {
                            slideInHorizontally(
                                initialOffsetX = { width -> width },
                                animationSpec = TweenSpec(durationMillis = durationMillis, easing = LinearEasing)
                            ) + fadeIn(
                                animationSpec = TweenSpec(durationMillis = durationMillis, easing = LinearEasing)
                            ) with fadeOut(
                                animationSpec = TweenSpec(
                                    durationMillis = durationMillis,
                                    easing = FastOutLinearInEasing
                                )
                            )
                        }
                    }
                }
            }.using(SizeTransform(clip = false))
        }
    ) { targetState -> TableContent(targetState) }
}

@Composable
private fun TableContent(tableState: TableState) {
    LazyRow(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier
            .fillMaxWidth()
            .testTag(UiTags.lastCardPlayed)
            .semantics(mergeDescendants = true) {}
    ) {
        items(tableState.cards()) { card ->
            Image(
                painter = painterResource(ResourceMapper.getImageResource(card)),
                contentDescription = tableState.contentDescription(),
                modifier = Modifier
                    .fillMaxWidth(0.25f) // Max 4 cards can be played at once (1/4 == 0.25)
                    .testTag(card.toString())
            )
        }
    }
}

@Composable
private fun PlayerDone(player: PlayerInfo) {
    val text = with(AnnotatedString.Builder()) {
        pushStyle(SpanStyle(textDecoration = TextDecoration.LineThrough))
        append(player.name)
        if (BuildConfig.DEBUG) {
            append(" (")
            append(stringResource(ResourceMapper.getImageResource(player.status)))
            append(")")
        }
        pop()
        toAnnotatedString()
    }
    PlayerInfoDisplay {
        Text(text)
    }
}

@Composable
private fun PlayerPlaying(player: PlayerInfo) {
    val status = stringResource(ResourceMapper.getImageResource(player.status))
    val text = if (BuildConfig.DEBUG) "${player.name} ($status)" else player.name
    PlayerInfoDisplay(MaterialTheme.colors.primary) { Text(text = text, color = Color.White) }
}

@Composable
private fun PlayerTurnPassed(player: PlayerInfo) {
    val text = with(AnnotatedString.Builder()) {
        pushStyle(SpanStyle(fontWeight = FontWeight.Light))
        append(player.name)
        if (BuildConfig.DEBUG) {
            append(" (")
            append(stringResource(ResourceMapper.getImageResource(player.status)))
            append(")")
        }
        pop()
        toAnnotatedString()
    }
    PlayerInfoDisplay { Text(text) }
}

@Composable
private fun PlayerWaiting(player: PlayerInfo) {
    val text = with(AnnotatedString.Builder()) {
        append(player.name)
        if (BuildConfig.DEBUG) {
            append(" (")
            append(stringResource(ResourceMapper.getImageResource(player.status)))
            append(")")
            if (player.dwitched) {
                append(" Dwitched")
            }
        }
        toAnnotatedString()
    }
    val backgroundColor = if (player.dwitched) MaterialTheme.colors.secondary else Color.White
    val textColor = if (player.dwitched) Color.White else Color.Black
    PlayerInfoDisplay(backgroundColor) { Text(text = text, color = textColor) }
}

@Composable
private fun PlayerInfoDisplay(
    backgroundColor: Color = Color.White,
    content: @Composable () -> Unit
) {
    val componentShape = RoundedCornerShape(5.dp, 5.dp, 5.dp, 5.dp)
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(color = backgroundColor, shape = componentShape),
        horizontalArrangement = Arrangement.Center
    ) {
        content()
    }
}
