package ch.qscqlmpa.dwitch.ui.ingame.gameroom.cardexchange

import ch.qscqlmpa.dwitch.ui.ingame.gameroom.CardInfo
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.game.DwitchCardExchange
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchstore.ingamestore.model.CardExchangeInfo
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class CardExchangeEngineTest {

    private lateinit var cardExchangeEngine: CardExchangeEngine
    private var numCardsToChoose: Int? = null
    private lateinit var allowedCardValues: List<CardName>
    private lateinit var cardsInHand: List<Card>

    @Test
    fun initialStateIsCorrectForTwoCards() {
        numCardsToChoose = 2
        allowedCardValues = listOf(CardName.Two, CardName.Five)
        cardsInHand = listOf(Card.Clubs2, Card.Hearts5, Card.Hearts4, Card.Spades5, Card.DiamondsAce)
        setupTest()

        assertThat(cardExchangeEngine.getCardExchangeState().canPerformExchange).isFalse
        assertThat(cardExchangeEngine.getCardExchangeState().cardsInHand).containsExactlyInAnyOrder(
            CardInfo(Card.Clubs2, selectable = true, selected = false),
            CardInfo(Card.DiamondsAce, selectable = false, selected = false),
            CardInfo(Card.Hearts5, selectable = true, selected = false),
            CardInfo(Card.Hearts4, selectable = false, selected = false),
            CardInfo(Card.Spades5, selectable = true, selected = false)
        )
    }

    @Test
    fun chooseTwoCardsToExchange() {
        numCardsToChoose = 2
        allowedCardValues = listOf(CardName.Two, CardName.Five)
        cardsInHand = listOf(Card.Clubs2, Card.Hearts5, Card.Hearts4, Card.Spades5, Card.DiamondsAce)
        setupTest()

        cardExchangeEngine.onCardToExchangeClick(Card.Clubs2)
        assertThat(cardExchangeEngine.getCardExchangeState().canPerformExchange).isFalse
        assertThat(cardExchangeEngine.getCardExchangeState().cardsInHand).containsExactlyInAnyOrder(
            CardInfo(Card.Clubs2, selectable = true, selected = true),
            CardInfo(Card.DiamondsAce, selectable = false, selected = false),
            CardInfo(Card.Hearts5, selectable = true, selected = false),
            CardInfo(Card.Hearts4, selectable = false, selected = false),
            CardInfo(Card.Spades5, selectable = true, selected = false)
        )

        cardExchangeEngine.onCardToExchangeClick(Card.Hearts5)
        assertThat(cardExchangeEngine.getCardExchangeState().canPerformExchange).isTrue
        assertThat(cardExchangeEngine.getCardExchangeState().cardsInHand).containsExactlyInAnyOrder(
            CardInfo(Card.Clubs2, selectable = true, selected = true),
            CardInfo(Card.DiamondsAce, selectable = false, selected = false),
            CardInfo(Card.Hearts5, selectable = true, selected = true),
            CardInfo(Card.Hearts4, selectable = false, selected = false),
            CardInfo(
                Card.Spades5,
                selectable = false,
                selected = false
            ) // selectable == false because only one card with CardName.Five can be selected
        )
    }

    @Test
    fun addAndRemoveCardsToExchange() {
        numCardsToChoose = 2
        allowedCardValues = listOf(CardName.Two, CardName.Five)
        cardsInHand = listOf(Card.Clubs2, Card.Hearts5, Card.Hearts4, Card.Spades5, Card.DiamondsAce)
        setupTest()

        cardExchangeEngine.onCardToExchangeClick(Card.Hearts5)
        cardExchangeEngine.onCardToExchangeClick(Card.Clubs2)
        assertThat(cardExchangeEngine.getCardExchangeState().canPerformExchange).isTrue
        assertThat(cardExchangeEngine.getCardExchangeState().cardsInHand).containsExactlyInAnyOrder(
            CardInfo(Card.Clubs2, selectable = true, selected = true),
            CardInfo(Card.DiamondsAce, selectable = false, selected = false),
            CardInfo(Card.Hearts5, selectable = true, selected = true),
            CardInfo(Card.Hearts4, selectable = false, selected = false),
            CardInfo(
                Card.Spades5,
                selectable = false,
                selected = false
            ) // selectable == false because only one card with CardName.Five can be selected
        )

        cardExchangeEngine.onCardToExchangeClick(Card.Hearts5)
        assertThat(cardExchangeEngine.getCardExchangeState().canPerformExchange).isFalse
        assertThat(cardExchangeEngine.getCardExchangeState().cardsInHand).containsExactlyInAnyOrder(
            CardInfo(Card.Clubs2, selectable = true, selected = true),
            CardInfo(Card.DiamondsAce, selectable = false, selected = false),
            CardInfo(Card.Hearts5, selectable = true, selected = false),
            CardInfo(Card.Hearts4, selectable = false, selected = false),
            CardInfo(Card.Spades5, selectable = true, selected = false)
        )
    }

    private fun setupTest() {
        cardExchangeEngine = CardExchangeEngine(
            CardExchangeInfo(
                DwitchCardExchange(
                    DwitchPlayerId(1),
                    numCardsToChoose = numCardsToChoose!!,
                    allowedCardValues = allowedCardValues
                ),
                cardsInHand = cardsInHand
            )
        )
    }
}
