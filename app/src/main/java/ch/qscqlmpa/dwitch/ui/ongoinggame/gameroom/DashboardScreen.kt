
import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.qscqlmpa.dwitch.ui.ResourceMapper
import ch.qscqlmpa.dwitch.ui.common.UiTags
import ch.qscqlmpa.dwitch.ui.ongoinggame.CardItemDisplay
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.info.DwitchCardInfo
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerStatus
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.GameDashboardInfo
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.LocalPlayerDashboard
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.PlayerInfo

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun DashboardScreenPreview() {
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

    val localPlayerDashboard = LocalPlayerDashboard(
        cardsInHand = listOf(
            DwitchCardInfo(Card.Clubs2, true),
            DwitchCardInfo(Card.Hearts5, false),
            DwitchCardInfo(Card.Diamonds8, true),
            DwitchCardInfo(Card.SpadesJack, true),
            DwitchCardInfo(Card.Clubs10, true),
            DwitchCardInfo(Card.Hearts4, false),
            DwitchCardInfo(Card.Hearts6, false),
            DwitchCardInfo(Card.Diamonds9, true),
            DwitchCardInfo(Card.SpadesAce, true),
            DwitchCardInfo(Card.ClubsJack, true),
            DwitchCardInfo(Card.Hearts7, false),
            DwitchCardInfo(Card.Spades3, false),
            DwitchCardInfo(Card.Clubs4, false),
            DwitchCardInfo(Card.Hearts8, true)

        ),
        canPass = false,
        canPickACard = true,
        canPlay = true
    )

    val gameDashboardInfo = GameDashboardInfo(
        playersInfo = players,
        localPlayerDashboard = localPlayerDashboard,
        lastCardPlayed = Card.Clubs8
    )

    DashboardScreen(
        dashboardInfo = gameDashboardInfo,
        onCardClick = {},
        onPickClick = {},
        onPassClick = {}
    )
}

@Composable
fun DashboardScreen(
    dashboardInfo: GameDashboardInfo,
    onCardClick: (Card) -> Unit,
    onPickClick: () -> Unit,
    onPassClick: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        val localPlayerDashboard = dashboardInfo.localPlayerDashboard
        PlayersInfo(dashboardInfo.playersInfo)
        Spacer(Modifier.height(16.dp))

        Table(dashboardInfo.lastCardPlayed)
        Spacer(Modifier.height(16.dp))

        Controls(
            localPlayerDashboard,
            onPickClick = onPickClick,
            onPassClick = onPassClick
        )
        Spacer(Modifier.height(16.dp))

        Hand(localPlayerDashboard, onCardClick = onCardClick)
    }
}

@Composable
private fun PlayersInfo(playersInfo: List<PlayerInfo>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
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

@Composable
private fun Table(lastCardPlayed: Card) {
    Row(
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        Image(
            painter = painterResource(ResourceMapper.getResource(lastCardPlayed)),
            contentDescription = lastCardPlayed.toString(),
            modifier = Modifier
                .size(150.dp)
                .testTag(UiTags.lastCardPlayer)
        )
    }
}

@Composable
private fun Controls(
    localPlayerDashboard: LocalPlayerDashboard,
    onPickClick: () -> Unit,
    onPassClick: () -> Unit
) {
    Row(
        horizontalArrangement = Arrangement.SpaceEvenly,
        modifier = Modifier.fillMaxWidth()
    ) {
        Button(
            enabled = localPlayerDashboard.canPickACard,
            onClick = onPickClick,
            modifier = Modifier.testTag(UiTags.pickACardControl)
        ) {
            Text(stringResource(ch.qscqlmpa.dwitch.R.string.pick_a_card))
        }
        Button(
            enabled = localPlayerDashboard.canPass,
            onClick = onPassClick,
            modifier = Modifier.testTag(UiTags.passControl)
        ) {
            Text(stringResource(ch.qscqlmpa.dwitch.R.string.pass_turn))
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun Hand(
    localPlayerDashboard: LocalPlayerDashboard,
    onCardClick: (Card) -> Unit
) {
    LazyVerticalGrid(
        cells = GridCells.Fixed(4),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
    ) {
        items(localPlayerDashboard.cardsInHand) { card -> CardItemDisplay(card, onCardClick) }
    }
}

@Composable
private fun PlayerDone(player: PlayerInfo) {
    val text = with(AnnotatedString.Builder()) {
        pushStyle(SpanStyle(textDecoration = TextDecoration.LineThrough))
        append(player.name)
        append(" (")
        append(stringResource(ResourceMapper.getResource(player.status)))
        append(")")
        pop()
        toAnnotatedString()
    }
    PlayerInfoDisplay {
        Text(text)
    }
}

@Composable
private fun PlayerPlaying(player: PlayerInfo) {
    val rank = stringResource(ResourceMapper.getResource(player.status))
    val text = "${player.name} ($rank)"
    PlayerInfoDisplay(MaterialTheme.colors.primary) {
        Text(text = text, color = Color.White)
    }
}

@Composable
private fun PlayerTurnPassed(player: PlayerInfo) {
    val text = with(AnnotatedString.Builder()) {
        pushStyle(SpanStyle(fontWeight = FontWeight.Light))
        append(player.name)
        append(" (")
        append(stringResource(ResourceMapper.getResource(player.status)))
        append(")")
        pop()
        toAnnotatedString()
    }
    PlayerInfoDisplay {
        Text(text)
    }
}

@Composable
private fun PlayerWaiting(player: PlayerInfo) {
    val text = with(AnnotatedString.Builder()) {
        append(player.name)
        append(" (")
        append(stringResource(ResourceMapper.getResource(player.status)))
        append(")")
        if (player.dwitched) {
            append(" Dwitched")
        }
        toAnnotatedString()
    }
    val backgroundColor = if (player.dwitched) MaterialTheme.colors.secondary else Color.White
    val textColor = if (player.dwitched) Color.White else Color.Black
    PlayerInfoDisplay(backgroundColor) {
        Text(text = text, color = textColor)
    }
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
        horizontalArrangement = Arrangement.Center,

        ) {
        content()
    }
}