package ch.qscqlmpa.dwitch.ui.ingame.gameroom.cardexchange

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material.FloatingActionButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.base.ActivityScreenContainer
import ch.qscqlmpa.dwitch.ui.common.UiTags
import ch.qscqlmpa.dwitch.ui.ingame.LoadingSpinner
import ch.qscqlmpa.dwitch.ui.ingame.PlayerHand
import ch.qscqlmpa.dwitch.ui.ingame.gameroom.CardInfo
import ch.qscqlmpa.dwitchengine.model.card.Card

@ExperimentalFoundationApi
@Preview(
    showBackground = true,
    backgroundColor = 0xFFFFFFFF
)
@Composable
private fun CardExchangeScreenPreview() {
    val cardsInHand = listOf(
        CardInfo(Card.Clubs2, selectable = true, selected = false),
        CardInfo(Card.HeartsAce, selectable = true, selected = true),
        CardInfo(Card.HeartsQueen, selectable = true, selected = false),
        CardInfo(Card.Diamonds10, selectable = true, selected = false),
        CardInfo(Card.Spades7, selectable = false, selected = false),
        CardInfo(Card.Spades5, selectable = false, selected = false),
        CardInfo(Card.Clubs4, selectable = false, selected = false),
        CardInfo(Card.Clubs3, selectable = false, selected = false)
    )

    ActivityScreenContainer {
        CardExchangeScreen(
            numCardsToChoose = NumCardsToExchange.Two,
            cardsInHand = cardsInHand,
            canSubmitCardsForExchange = true,
            onCardClick = {},
            onConfirmExchangeClick = {}
        )
    }
}

@ExperimentalFoundationApi
@Composable
fun CardExchangeScreen(
    numCardsToChoose: NumCardsToExchange,
    cardsInHand: List<CardInfo>,
    canSubmitCardsForExchange: Boolean,
    onCardClick: (Card) -> Unit,
    onConfirmExchangeClick: () -> Unit
) {
    Column(
        Modifier
            .fillMaxWidth()
            .animateContentSize()
    ) {
        val chooseCardLabel = when (numCardsToChoose) {
            NumCardsToExchange.One -> R.string.choose_one_card_to_exchange
            NumCardsToExchange.Two -> R.string.choose_two_cards_to_exchange
        }
        Text(stringResource(chooseCardLabel))
        Spacer(Modifier.height(16.dp))
        Spacer(Modifier.height(16.dp))

        if (canSubmitCardsForExchange) {
            FloatingActionButton(
                backgroundColor = MaterialTheme.colors.primary,
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .testTag(UiTags.confirmCardExchange),
                onClick = onConfirmExchangeClick
            ) {
                Text(text = stringResource(R.string.confirm_cards_to_exchange), color = Color.White)
            }
        }

        PlayerHand(cardsInHand, onCardClick = onCardClick)
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
