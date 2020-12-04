package ch.qscqlmpa.dwitchengine.rules

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.player.Rank
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test

internal class CardExchangeComputerTest {

    private val someCards = listOf(Card.Clubs2, Card.Spades2, Card.Clubs3, Card.Diamonds10, Card.SpadesAce, Card.Hearts5)

    @Test
    fun `President must choose 2 cards`() {
        val cardExchange = CardExchangeComputer.getCardExchange(Rank.President, someCards)
        assertThat(cardExchange.numCardsToChoose).isEqualTo(2)
    }

    @Test
    fun `Vice-President must choose 1 card`() {
        val cardExchange = CardExchangeComputer.getCardExchange(Rank.VicePresident, someCards)
        assertThat(cardExchange.numCardsToChoose).isEqualTo(1)
    }

    @Test
    fun `Vice-Asshole must choose 1 card`() {
        val cardExchange = CardExchangeComputer.getCardExchange(Rank.ViceAsshole, someCards)
        assertThat(cardExchange.numCardsToChoose).isEqualTo(1)
    }

    @Test
    fun `Asshole must choose 2 cards`() {
        val cardExchange = CardExchangeComputer.getCardExchange(Rank.Asshole, someCards)
        assertThat(cardExchange.numCardsToChoose).isEqualTo(2)
    }

    @Test
    fun `Neutral players don't perform any card exchange`() {
        assertThrows(IllegalArgumentException::class.java) { CardExchangeComputer.getCardExchange(Rank.Neutral, someCards) }
    }

    @Test
    fun `President can choose any of its cards`() {
        val cards = listOf(Card.Clubs2, Card.Spades2, Card.Clubs3, Card.Diamonds10, Card.SpadesAce, Card.Hearts5)

        val cardExchange = CardExchangeComputer.getCardExchange(Rank.President, cards)

        assertThat(cardExchange.allowedCardValues).containsExactlyInAnyOrder(CardName.Two, CardName.Two, CardName.Three, CardName.Ten, CardName.Ace, CardName.Five)
    }

    @Test
    fun `Vice-President can choose any of its cards`() {
        val cards = listOf(Card.Clubs2, Card.Spades2, Card.Clubs3, Card.Diamonds10, Card.SpadesAce, Card.Hearts5)

        val cardExchange = CardExchangeComputer.getCardExchange(Rank.VicePresident, cards)

        assertThat(cardExchange.allowedCardValues).containsExactlyInAnyOrder(CardName.Two, CardName.Two, CardName.Three, CardName.Ten, CardName.Ace, CardName.Five)
    }

    @Test
    fun `Asshole must choose the 2 cards with the highest value - twice same value`() {
        val cards = listOf(Card.Clubs3, Card.Diamonds10, Card.SpadesAce, Card.Hearts5, Card.HeartsAce)

        val cardExchange = CardExchangeComputer.getCardExchange(Rank.Asshole, cards)

        assertThat(cardExchange.allowedCardValues).containsExactlyInAnyOrder(CardName.Ace, CardName.Ace)
    }

    @Test
    fun `Asshole must choose the 2 cards with the highest value - different values`() {
        val cards = listOf(Card.Clubs3, Card.Diamonds10, Card.SpadesAce, Card.Hearts5)

        val cardExchange = CardExchangeComputer.getCardExchange(Rank.Asshole, cards)

        assertThat(cardExchange.allowedCardValues).containsExactlyInAnyOrder(CardName.Ten, CardName.Ace)
    }

    @Test
    fun `Asshole must choose the jokers when it has some (the joker has the highest value) - two jokers`() {
        val cards = listOf(Card.Clubs2, Card.Spades2, Card.Clubs3, Card.Diamonds10, Card.SpadesAce, Card.Hearts5)

        val cardExchange = CardExchangeComputer.getCardExchange(Rank.Asshole, cards)

        assertThat(cardExchange.allowedCardValues).containsExactlyInAnyOrder(INITIAL_JOKER, INITIAL_JOKER)
    }

    @Test
    fun `Asshole must choose the jokers when it has some (the joker has the highest value) - one joker`() {
        val cards = listOf(Card.Clubs2, Card.Spades2, Card.Clubs3, Card.Diamonds10, Card.SpadesAce, Card.Hearts5)

        val cardExchange = CardExchangeComputer.getCardExchange(Rank.Asshole, cards)

        assertThat(cardExchange.allowedCardValues).containsExactlyInAnyOrder(INITIAL_JOKER, CardName.Ace)
    }

    @Test
    fun `Vice-Asshole must choose the card with the highest value`() {
        val cards = listOf(Card.Clubs3, Card.Diamonds10, Card.SpadesAce, Card.Hearts5, Card.HeartsAce)

        val cardExchange = CardExchangeComputer.getCardExchange(Rank.ViceAsshole, cards)

        assertThat(cardExchange.allowedCardValues).containsExactlyInAnyOrder(CardName.Ace, CardName.Ace)
    }

    @Test
    fun `Vice-Asshole must choose the joker when it has some (the joker has the highest value)`() {
        val cards = listOf(Card.Clubs2, Card.Spades2, Card.Clubs3, Card.Diamonds10, Card.SpadesAce, Card.Hearts5)

        val cardExchange = CardExchangeComputer.getCardExchange(Rank.ViceAsshole, cards)

        assertThat(cardExchange.allowedCardValues).containsExactlyInAnyOrder(INITIAL_JOKER, CardName.Ace)
    }
}