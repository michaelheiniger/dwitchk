package ch.qscqlmpa.dwitch.ui.ongoinggame.cardexchange

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.GridCells
import androidx.compose.foundation.lazy.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.material.Button
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.ResourceMapper
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
        CardItem(Card.HeartsQueen, false),
        CardItem(Card.Clubs3, false),
        CardItem(Card.Clubs4, false),
        CardItem(Card.Diamonds10, false),
        CardItem(Card.Spades5, false),
        CardItem(Card.Spades7, false)
    )

    CardExchangeScreen(
        cardsToExchange = cardsToExchange,
        cardsInHand = cardsInHand,
        exchangeControlEnabled = true,
        onCardToExchangeClick = {},
        onCardInHandClick = {},
        onExchangeClick = {}
    )
}

@ExperimentalFoundationApi
@Composable
fun CardExchangeScreen(
    cardsToExchange: List<CardItem>,
    cardsInHand: List<CardItem>,
    exchangeControlEnabled: Boolean,
    onCardToExchangeClick: (Card) -> Unit,
    onCardInHandClick: (Card) -> Unit,
    onExchangeClick: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .animateContentSize()
            .padding(top = 16.dp, start = 16.dp, end = 16.dp)

    ) {
        CardsToExchange(
            cards = cardsToExchange,
            exchangeControlEnabled = exchangeControlEnabled,
            onExchangeClick = onExchangeClick,
            onCardClick = onCardToExchangeClick
        )
        CardsInHand(
            cards = cardsInHand,
            onCardClick = onCardInHandClick
        )
    }
}

@ExperimentalFoundationApi
@Composable
private fun CardsToExchange(
    cards: List<CardItem>,
    exchangeControlEnabled: Boolean,
    onExchangeClick: () -> Unit,
    onCardClick: (Card) -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        Row(Modifier.fillMaxWidth()) {
            Text(text = stringResource(R.string.choose_cards_to_exchange))
            Button(
                enabled = exchangeControlEnabled,
                onClick = onExchangeClick
            ) {
                Text(text = stringResource(R.string.confirm_cards_to_exchange))
            }
        }

        LazyVerticalGrid(
            cells = GridCells.Fixed(4),
            Modifier
                .fillMaxWidth()
                .animateContentSize()
        ) {
            items(cards) { card ->
                CardItemDisplay(
                    cardItem = card,
                    onCardClick = onCardClick
                )
            }
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
        Text(text = stringResource(R.string.cards_in_hand))
        LazyVerticalGrid(
            cells = GridCells.Fixed(4),
            Modifier
                .fillMaxWidth()
                .animateContentSize()
        ) {
            items(cards) { card ->
                CardItemDisplay(
                    cardItem = card,
                    onCardClick = onCardClick
                )
            }
        }
    }
}

@Composable
private fun CardItemDisplay(
    cardItem: CardItem,
    onCardClick: (Card) -> Unit
) {
    Image(
        painter = painterResource(ResourceMapper.getResource(cardItem.card)),
        contentDescription = cardItem.toString(),
        Modifier.clickable { onCardClick(cardItem.card) }
    )
}