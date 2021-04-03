import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.qscqlmpa.dwitch.ui.ResourceMapper
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.info.CardItem
import ch.qscqlmpa.dwitchengine.model.player.PlayerStatus
import ch.qscqlmpa.dwitchengine.model.player.Rank
import ch.qscqlmpa.dwitchgame.ongoinggame.game.GameDashboardInfo
import ch.qscqlmpa.dwitchgame.ongoinggame.game.LocalPlayerDashboard
import ch.qscqlmpa.dwitchgame.ongoinggame.game.PlayerInfo2

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun DashboardScreenPreview() {
    val donePlayer = PlayerInfo2(
        name = "Aragorn",
        rank = Rank.VicePresident,
        status = PlayerStatus.Done,
        dwitched = false,
        localPlayer = false
    )

    val turnPassedPlayer = PlayerInfo2(
        name = "Gimli",
        rank = Rank.ViceAsshole,
        status = PlayerStatus.TurnPassed,
        dwitched = false,
        localPlayer = false
    )

    // Also the local player
    val playingPlayer = PlayerInfo2(
        name = "Legolas",
        rank = Rank.Asshole,
        status = PlayerStatus.Playing,
        dwitched = false,
        localPlayer = true
    )

    // Waiting
    val waitingPlayer = PlayerInfo2(
        name = "Elrond",
        rank = Rank.President,
        status = PlayerStatus.Waiting,
        dwitched = false,
        localPlayer = false
    )

    val waitingAndDwitchedPlayer = PlayerInfo2(
        name = "Galadriel",
        rank = Rank.Neutral,
        status = PlayerStatus.Waiting,
        dwitched = true,
        localPlayer = false
    )

    val players = listOf(donePlayer, turnPassedPlayer, playingPlayer, waitingPlayer, waitingAndDwitchedPlayer)

    val localPlayerDashboard = LocalPlayerDashboard(
        dashboardEnabled = true,
        cardsInHand = listOf(
            CardItem(Card.Clubs2, true),
            CardItem(Card.Hearts5, false),
            CardItem(Card.Diamonds8, true),
            CardItem(Card.SpadesJack, true),
            CardItem(Card.Clubs10, true),
            CardItem(Card.Hearts4, false),
            CardItem(Card.Hearts6, false),
            CardItem(Card.Diamonds9, true),
            CardItem(Card.SpadesAce, true),
            CardItem(Card.ClubsJack, true),
            CardItem(Card.Hearts7, false),
            CardItem(Card.Spades3, false),
            CardItem(Card.Clubs4, false),
            CardItem(Card.Hearts8, true)

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

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun DashboardLoadingScreenPreview() {
    DashboardScreen(null, {}, {}, {})
}

@Composable
fun DashboardScreen(
    dashboardInfo: GameDashboardInfo?,
    onCardClick: (Card) -> Unit,
    onPickClick: () -> Unit,
    onPassClick: () -> Unit
) {
    if (dashboardInfo != null) {
        Column(
            Modifier
                .fillMaxWidth()
                .animateContentSize()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)
        ) {
            val localPlayerDashboard = dashboardInfo.localPlayerDashboard

            PlayersInfo(dashboardInfo.playersInfo)

            Spacer(modifier = Modifier.height(16.dp))

            Table(dashboardInfo.lastCardPlayed)

            Spacer(modifier = Modifier.height(16.dp))

            Controls(
                localPlayerDashboard,
                onPickClick = onPickClick,
                onPassClick = onPassClick
            )

            Spacer(modifier = Modifier.height(16.dp))

            Hand(localPlayerDashboard, onCardClick = onCardClick)
        }
    } else {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .fillMaxHeight()
                .animateContentSize()
                .padding(top = 16.dp, start = 16.dp, end = 16.dp)

        ) {
            CircularProgressIndicator(
                color = MaterialTheme.colors.secondary
            )
        }
    }
}

@Composable
private fun PlayersInfo(playersInfo: List<PlayerInfo2>) {
    Column(
        modifier = Modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        for (player in playersInfo) {
            when (player.status) {
                PlayerStatus.Done -> PlayerDone(player)
                PlayerStatus.Playing -> PlayerPlaying(player)
                PlayerStatus.TurnPassed -> PlayerTurnPassed(player)
                PlayerStatus.Waiting -> PlayerWaiting(player)
            }
            Spacer(modifier = Modifier.height(4.dp))
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
            modifier = Modifier.size(150.dp)
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
            enabled = localPlayerDashboard.dashboardEnabled && localPlayerDashboard.canPickACard,
            onClick = onPickClick
        ) {
            Text(stringResource(id = ch.qscqlmpa.dwitch.R.string.pdf_pick_a_card))
        }
        Button(
            enabled = localPlayerDashboard.dashboardEnabled && localPlayerDashboard.canPass,
            onClick = onPassClick
        ) {
            Text(stringResource(id = ch.qscqlmpa.dwitch.R.string.pdf_pass))
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
        items(localPlayerDashboard.cardsInHand) { card ->
            CardItemDisplay(card, localPlayerDashboard.dashboardEnabled, onCardClick = onCardClick)
        }
    }
}

@Composable
private fun CardItemDisplay(
    cardItem: CardItem,
    dashboardEnabled: Boolean,
    onCardClick: (Card) -> Unit
) {
    val surfaceColor = if (dashboardEnabled && cardItem.selectable) Color.Transparent else Color(
        red = 0.3f,
        green = 0.3f,
        blue = 0.3f,
        alpha = 0.2f
    )
    Image(
        painter = painterResource(ResourceMapper.getResource(cardItem.card)),
        contentDescription = cardItem.toString(),
        colorFilter = ColorFilter.tint(surfaceColor, BlendMode.Overlay),
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .clickable {
                if (dashboardEnabled && cardItem.selectable) {
                    onCardClick(cardItem.card)
                }
            }
//            .border(
//                width = Dp.Hairline,
//                brush = Brush.linearGradient(listOf(Color.Black, Color.Black)),
//                shape = RoundedCornerShape(10.dp, 10.dp, 10.dp, 10.dp)
//            )
    )
}

@Composable
private fun PlayerDone(player: PlayerInfo2) {
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
private fun PlayerPlaying(player: PlayerInfo2) {
    val rank = stringResource(ResourceMapper.getResource(player.status))
    val text = "${player.name} ($rank)"
    PlayerInfoDisplay(MaterialTheme.colors.primary) {
        Text(text = text, color = Color.White)
    }
}

@Composable
private fun PlayerTurnPassed(player: PlayerInfo2) {
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
private fun PlayerWaiting(player: PlayerInfo2) {
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
            .background(
                color = backgroundColor,
                shape = componentShape
            ),
        horizontalArrangement = Arrangement.Center,

        ) {
        content()
    }
}