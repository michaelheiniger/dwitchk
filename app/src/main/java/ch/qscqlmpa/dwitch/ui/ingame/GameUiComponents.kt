package ch.qscqlmpa.dwitch.ui.ingame

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.ResourceMapper
import ch.qscqlmpa.dwitch.ui.base.ActivityScreenContainer
import ch.qscqlmpa.dwitch.ui.common.InfoDialog
import ch.qscqlmpa.dwitch.ui.common.UiTags
import ch.qscqlmpa.dwitch.ui.ingame.gameroom.CardInfo
import ch.qscqlmpa.dwitchengine.model.card.Card

@Composable
fun GameOverDialog(onGameOverAcknowledge: () -> Unit) {
    InfoDialog(
        title = R.string.info_dialog_title,
        text = R.string.game_over,
        onOkClick = onGameOverAcknowledge
    )
}

@Composable
fun LoadingSpinner() {
    Column(
        Modifier
            .fillMaxWidth()
            .animateContentSize(),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CircularProgressIndicator(
            color = MaterialTheme.colors.secondary
        )
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun PlayerHand(
    cardsInHand: List<CardInfo>,
    onCardClick: (Card) -> Unit
) {
    LazyVerticalGrid(
        cells = GridCells.Fixed(4),
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .testTag(UiTags.hand)
    ) {
        items(cardsInHand) { card -> CardItemDisplay(card, onCardClick) }
    }
}

@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun CardItemDisplayPreview() {
    ActivityScreenContainer {
        CardItemDisplay(cardItem = CardInfo(Card.Hearts10, selectable = true, selected = true), onCardClick = {})
    }
}

@Composable
fun CardItemDisplay(
    cardItem: CardInfo,
    onCardClick: (Card) -> Unit
) {
    val surfaceColor = if (cardItem.selected) MaterialTheme.colors.secondary else Color.Transparent
    val alpha = if (cardItem.selectable) 1.0f else 0.2f
    Image(
        painter = painterResource(ResourceMapper.getImageResource(cardItem.card)),
        contentDescription = stringResource(ResourceMapper.getContentDescriptionResource(cardItem.card)),
        colorFilter = ColorFilter.tint(surfaceColor, BlendMode.Overlay),
        contentScale = ContentScale.Fit,
        modifier = Modifier
            .clickable(
                enabled = cardItem.selectable,
                onClick = { onCardClick(cardItem.card) },
                onClickLabel = "", //TODO
            )
            .testTag(cardItem.card.toString())
            .alpha(alpha)
    )
}

@Composable
fun GameRulesDialog(onOkClick: () -> Unit) {
    InfoDialog(
        title = R.string.game_rules_info_title,
        text = R.string.game_rules_info_content,
        onOkClick = onOkClick
    )
}