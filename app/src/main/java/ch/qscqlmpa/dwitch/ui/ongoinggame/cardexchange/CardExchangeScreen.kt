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
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.ongoinggame.CardItemDisplay
import ch.qscqlmpa.dwitch.ui.ongoinggame.LoadingSpinner
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.info.CardItem

@ExperimentalFoundationApi
@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun CardExchangeScreenPreview() {
    val cardsToExchange = listOf(
        CardItem(Card.HeartsAce, true),
        CardItem(Card.Clubs2, true),
    )
    val cardsInHand = listOf(
        CardItem(Card.HeartsQueen, true),
        CardItem(Card.Clubs3, false),
        CardItem(Card.Clubs4, false),
        CardItem(Card.Diamonds10, true),
        CardItem(Card.Spades5, false),
        CardItem(Card.Spades7, false)
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
    cardsToExchange: List<CardItem>,
    cardsInHand: List<CardItem>,
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

        CardsInHand(
            cards = cardsInHand,
            onCardClick = onCardInHandClick
        )
        Spacer(Modifier.height(16.dp))
        Button(
            enabled = exchangeControlEnabled,
            onClick = onConfirmExchangeClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.confirm_cards_to_exchange))
        }
    }
}

@Composable
fun CardExchangeOnGoing() {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .animateContentSize(),
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
    cards: List<CardItem>,
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
    cards: List<CardItem>,
    onCardClick: (Card) -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .animateContentSize()
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