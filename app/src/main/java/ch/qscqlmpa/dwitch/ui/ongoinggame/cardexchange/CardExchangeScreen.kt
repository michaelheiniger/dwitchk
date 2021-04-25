package ch.qscqlmpa.dwitch.ui.ongoinggame.cardexchange

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.common.UiTags
import ch.qscqlmpa.dwitch.ui.ongoinggame.CardItemDisplay
import ch.qscqlmpa.dwitch.ui.ongoinggame.LoadingSpinner
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.info.DwitchCardInfo

@ExperimentalFoundationApi
@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun CardExchangeScreenPreview() {
    val cardsToExchange = listOf(
        DwitchCardInfo(Card.HeartsAce, true),
        DwitchCardInfo(Card.Clubs2, true),
    )
    val cardsInHand = listOf(
        DwitchCardInfo(Card.HeartsQueen, true),
        DwitchCardInfo(Card.Clubs3, false),
        DwitchCardInfo(Card.Clubs4, false),
        DwitchCardInfo(Card.Diamonds10, true),
        DwitchCardInfo(Card.Spades5, false),
        DwitchCardInfo(Card.Spades7, false)
    )

    CardExchangeScreen(
        numCardsToChoose = NumCardForExchange.Two,
        cardsToExchange = cardsToExchange,
        cardsInHand = cardsInHand,
        exchangeControlEnabled = true,
        onCardToExchangeClick = {},
        onCardInHandClick = {},
        onConfirmExchangeClick = {}
    )
}

@ExperimentalFoundationApi
@Composable
fun CardExchangeScreen(
    numCardsToChoose: NumCardForExchange,
    cardsToExchange: List<DwitchCardInfo>,
    cardsInHand: List<DwitchCardInfo>,
    exchangeControlEnabled: Boolean,
    onCardToExchangeClick: (Card) -> Unit,
    onCardInHandClick: (Card) -> Unit,
    onConfirmExchangeClick: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        CardsToExchange(
            numCardsToChoose = numCardsToChoose,
            cards = cardsToExchange,
            onCardClick = onCardToExchangeClick
        )
        Spacer(Modifier.height(16.dp))
        Button(
            enabled = exchangeControlEnabled,
            onClick = onConfirmExchangeClick,
            modifier = Modifier
                .fillMaxWidth()
                .testTag(UiTags.confirmCardExchange)
        ) { Text(stringResource(R.string.confirm_cards_to_exchange)) }
        Spacer(Modifier.height(16.dp))
        CardsInHand(
            cards = cardsInHand,
            onCardClick = onCardInHandClick
        )
    }
}

@Composable
fun CardExchangeOnGoing() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize()
            .testTag(UiTags.cardExchangeOngoing),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Card exchange is ongoing ...")
        LoadingSpinner()
    }
}

@ExperimentalFoundationApi
@Composable
private fun CardsToExchange(
    numCardsToChoose: NumCardForExchange,
    cards: List<DwitchCardInfo>,
    onCardClick: (Card) -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        val chooseCardLabel = when (numCardsToChoose) {
            NumCardForExchange.One -> R.string.choose_one_card_to_exchange
            NumCardForExchange.Two -> R.string.choose_two_cards_to_exchange
        }
        Text(stringResource(chooseCardLabel))
        LazyVerticalGrid(
            cells = GridCells.Fixed(4),
            Modifier
                .fillMaxWidth()
                .animateContentSize()
        ) {
            items(cards) { card -> CardItemDisplay(card, onCardClick) }
        }
    }
}

@ExperimentalFoundationApi
@Composable
private fun CardsInHand(
    cards: List<DwitchCardInfo>,
    onCardClick: (Card) -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .animateContentSize()
            .testTag(UiTags.hand)
    ) {
        Text(stringResource(R.string.cards_in_hand))
        LazyVerticalGrid(
            cells = GridCells.Fixed(4),
            Modifier
                .fillMaxWidth()
                .animateContentSize()
        ) {
            items(cards) { card -> CardItemDisplay(cardItem = card, onCardClick = onCardClick) }
        }
    }
}
