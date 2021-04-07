package ch.qscqlmpa.dwitch.ui.ongoinggame.cardexchange

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.game.CardExchange
import ch.qscqlmpa.dwitchengine.model.info.CardItem
import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId
import ch.qscqlmpa.dwitchstore.ingamestore.model.CardExchangeInfo
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class CardExchangeStateEngineTest {

    private lateinit var cardExchangeStateEngine: CardExchangeStateEngine
    private var numCardsToChoose: Int? = null
    private lateinit var allowedCardValues: List<CardName>
    private lateinit var cardsInHand: List<Card>

    @Test
    fun initialStateIsCorrectForTwoCards() {
        numCardsToChoose = 2
        allowedCardValues = listOf(CardName.Two, CardName.Five)
        cardsInHand = listOf(Card.Clubs2, Card.Hearts5, Card.Hearts4, Card.Spades5, Card.DiamondsAce)
        setupTest()

        assertThat(cardExchangeStateEngine.getCardExchangeState().canPerformExchange).isFalse
        assertThat(cardExchangeStateEngine.getCardExchangeState().cardsInHand).containsExactlyInAnyOrder(
            CardItem(Card.Clubs2, true),
            CardItem(Card.DiamondsAce, false),
            CardItem(Card.Hearts5, true),
            CardItem(Card.Hearts4, false),
            CardItem(Card.Spades5, true)
        )
        assertThat(cardExchangeStateEngine.getCardExchangeState().cardsToExchange).isEmpty()
    }

    @Test
    fun chooseTwoCardsToExchange() {
        numCardsToChoose = 2
        allowedCardValues = listOf(CardName.Two, CardName.Five)
        cardsInHand = listOf(Card.Clubs2, Card.Hearts5, Card.Hearts4, Card.Spades5, Card.DiamondsAce)
        setupTest()

        cardExchangeStateEngine.addCardToExchange(Card.Clubs2)
        assertThat(cardExchangeStateEngine.getCardExchangeState().canPerformExchange).isFalse
        assertThat(cardExchangeStateEngine.getCardExchangeState().cardsInHand).containsExactlyInAnyOrder(
            CardItem(Card.DiamondsAce, false),
            CardItem(Card.Hearts5, true),
            CardItem(Card.Hearts4, false),
            CardItem(Card.Spades5, true)
        )
        assertThat(cardExchangeStateEngine.getCardExchangeState().cardsToExchange).containsExactlyInAnyOrder(CardItem(Card.Clubs2))

        cardExchangeStateEngine.addCardToExchange(Card.Hearts5)
        assertThat(cardExchangeStateEngine.getCardExchangeState().canPerformExchange).isTrue
        assertThat(cardExchangeStateEngine.getCardExchangeState().cardsInHand).containsExactlyInAnyOrder(
            CardItem(Card.DiamondsAce, false),
            CardItem(Card.Hearts4, false),
            CardItem(Card.Spades5, false) // false because only one card with CardName.Five can be selected
        )
        assertThat(cardExchangeStateEngine.getCardExchangeState().cardsToExchange).containsExactlyInAnyOrder(
            CardItem(Card.Clubs2),
            CardItem(Card.Hearts5)
        )
    }

    @Test
    fun addAndRemoveCardsToExchange() {
        numCardsToChoose = 2
        allowedCardValues = listOf(CardName.Two, CardName.Five)
        cardsInHand = listOf(Card.Clubs2, Card.Hearts5, Card.Hearts4, Card.Spades5, Card.DiamondsAce)
        setupTest()

        cardExchangeStateEngine.addCardToExchange(Card.Hearts5)
        cardExchangeStateEngine.addCardToExchange(Card.Clubs2)
        assertThat(cardExchangeStateEngine.getCardExchangeState().canPerformExchange).isTrue
        assertThat(cardExchangeStateEngine.getCardExchangeState().cardsInHand).containsExactlyInAnyOrder(
            CardItem(Card.DiamondsAce, false),
            CardItem(Card.Hearts4, false),
            CardItem(Card.Spades5, false) // false because only one card with CardName.Five can be selected
        )
        assertThat(cardExchangeStateEngine.getCardExchangeState().cardsToExchange).containsExactlyInAnyOrder(
            CardItem(Card.Clubs2),
            CardItem(Card.Hearts5)
        )

        cardExchangeStateEngine.removeCardFromExchange(Card.Hearts5)
        assertThat(cardExchangeStateEngine.getCardExchangeState().canPerformExchange).isFalse
        assertThat(cardExchangeStateEngine.getCardExchangeState().cardsInHand).containsExactlyInAnyOrder(
            CardItem(Card.DiamondsAce, false),
            CardItem(Card.Hearts4, false),
            CardItem(Card.Hearts5, true),
            CardItem(Card.Spades5, true)
        )
        assertThat(cardExchangeStateEngine.getCardExchangeState().cardsToExchange).containsExactlyInAnyOrder(
            CardItem(Card.Clubs2)
        )
    }

    private fun setupTest() {
        cardExchangeStateEngine = CardExchangeStateEngine(
            CardExchangeInfo(
                CardExchange(
                    PlayerDwitchId(1),
                    numCardsToChoose = numCardsToChoose!!,
                    allowedCardValues = allowedCardValues
                ),
                cardsInHand = cardsInHand
            )
        )
    }
}