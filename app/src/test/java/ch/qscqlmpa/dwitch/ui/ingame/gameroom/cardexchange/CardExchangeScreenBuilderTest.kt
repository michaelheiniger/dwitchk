package ch.qscqlmpa.dwitch.ui.ingame.gameroom.cardexchange

import ch.qscqlmpa.dwitch.ui.ingame.gameroom.CardInfo
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.game.DwitchCardExchange
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchstore.ingamestore.model.CardExchangeInfo
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class CardExchangeScreenBuilderTest {

    private lateinit var cardExchangeScreenBuilder: CardExchangeScreenBuilder

    private val cardExchangeInfo = CardExchangeInfo(
        DwitchCardExchange(
            playerId = DwitchPlayerId(1),
            numCardsToChoose = 2,
            allowedCardValues = listOf(CardName.Two, CardName.Jack)
        ),
        cardsInHand = listOf(Card.Clubs3, Card.Diamonds5, Card.Clubs2, Card.HeartsJack)
    )

    @Before
    fun setup() {
        cardExchangeScreenBuilder = CardExchangeScreenBuilder(cardExchangeInfo)
    }

    @Test
    fun `Initial screen is generated according to card exchange info`() {
        // Given initial card exchange info
        // Then the screen is properly built
        val cardExchangeState = cardExchangeScreenBuilder.screen.cardExchangeState
        assertThat(cardExchangeState.canPerformExchange).isFalse
        assertThat(cardExchangeState.cardsInHand).containsExactlyInAnyOrder(
            CardInfo(Card.Clubs3, selectable = false, selected = false),
            CardInfo(Card.Diamonds5, selectable = false, selected = false),
            CardInfo(Card.Clubs2, selectable = true, selected = false),
            CardInfo(Card.HeartsJack, selectable = true, selected = false)
        )
        assertThat(cardExchangeState.numCardsToChoose).isEqualTo(NumCardsToExchange.Two)
    }

    @Test
    fun `Cards in hand are sorted by value desc`() {
        // Given unsorted cards
        assertThat(cardExchangeInfo.cardsInHand[0]).isEqualTo(Card.Clubs3)
        assertThat(cardExchangeInfo.cardsInHand[1]).isEqualTo(Card.Diamonds5)

        // When querying the screen, then the cards are sorted
        val cardsInHand = cardExchangeScreenBuilder.screen.cardExchangeState.cardsInHand

        // Then the cards are sorted
        assertThat(cardsInHand.size).isEqualTo(4)
        assertThat(cardsInHand[0].card.name).isEqualTo(CardName.Two) // Joker is considered having the highest value
        assertThat(cardsInHand[1].card.name).isEqualTo(CardName.Jack)
        assertThat(cardsInHand[2].card.name).isEqualTo(CardName.Five)
        assertThat(cardsInHand[3].card.name).isEqualTo(CardName.Three)
    }

    @Test
    fun `Click on card updates the screen`() {
        // Given no card is selected
        assertThat(cardExchangeScreenBuilder.selectedCards).isEmpty()
        assertThat(cardExchangeScreenBuilder.screen.cardExchangeState.cardsInHand).noneMatch { c -> c.selected }

        // When a non-selected card is clicked
        val clickedCard = Card.Clubs2
        cardExchangeScreenBuilder.onCardClick(clickedCard)

        // Then it becomes selected
        assertThat(cardExchangeScreenBuilder.selectedCards).containsExactly(clickedCard)
        assertThat(cardExchangeScreenBuilder.screen.cardExchangeState.cardsInHand.filter { c -> c.card != clickedCard }).noneMatch { c -> c.selected }
        assertThat(cardExchangeScreenBuilder.screen.cardExchangeState.cardsInHand.filter { c -> c.card == clickedCard }).allMatch { c -> c.selected }
    }
}
