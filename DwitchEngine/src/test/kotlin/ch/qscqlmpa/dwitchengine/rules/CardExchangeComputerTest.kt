package ch.qscqlmpa.dwitchengine.rules

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

internal class CardExchangeComputerTest {

    private val someCards = setOf(Card.Clubs2, Card.Spades2, Card.Clubs3, Card.Diamonds10, Card.SpadesAce, Card.Hearts5)

    private val playerId = DwitchPlayerId(2)

    @Test
    fun `President must choose 2 cards`() {
        val cardExchange = CardExchangeComputer.getCardExchange(playerId, DwitchRank.President, someCards)
        assertThat(cardExchange!!.numCardsToChoose).isEqualTo(2)
    }

    @Test
    fun `Vice-President must choose 1 card`() {
        val cardExchange = CardExchangeComputer.getCardExchange(playerId, DwitchRank.VicePresident, someCards)
        assertThat(cardExchange!!.numCardsToChoose).isEqualTo(1)
    }

    @Test
    fun `Vice-Asshole must choose 1 card`() {
        val cardExchange = CardExchangeComputer.getCardExchange(playerId, DwitchRank.ViceAsshole, someCards)
        assertThat(cardExchange!!.numCardsToChoose).isEqualTo(1)
    }

    @Test
    fun `Asshole must choose 2 cards`() {
        val cardExchange = CardExchangeComputer.getCardExchange(playerId, DwitchRank.Asshole, someCards)
        assertThat(cardExchange!!.numCardsToChoose).isEqualTo(2)
    }

    @Test
    fun `Neutral players don't perform any card exchange`() {
        val cardExchange = CardExchangeComputer.getCardExchange(playerId, DwitchRank.Neutral, someCards)
        assertThat(cardExchange).isNull()
    }

    @Test
    fun `President can choose any of its cards`() {
        val cards = setOf(Card.Clubs2, Card.Spades2, Card.Clubs3, Card.Diamonds10, Card.SpadesAce, Card.Hearts5)

        val cardExchange = CardExchangeComputer.getCardExchange(playerId, DwitchRank.President, cards)

        assertThat(cardExchange!!.allowedCardValues).containsExactlyInAnyOrder(CardName.Two, CardName.Two, CardName.Three, CardName.Ten, CardName.Ace, CardName.Five)
    }

    @Test
    fun `Vice-President can choose any of its cards`() {
        val cards = setOf(Card.Clubs2, Card.Spades2, Card.Clubs3, Card.Diamonds10, Card.SpadesAce, Card.Hearts5)

        val cardExchange = CardExchangeComputer.getCardExchange(playerId, DwitchRank.VicePresident, cards)

        assertThat(cardExchange!!.allowedCardValues).containsExactlyInAnyOrder(CardName.Two, CardName.Two, CardName.Three, CardName.Ten, CardName.Ace, CardName.Five)
    }

    @Test
    fun `Asshole must choose the 2 cards with the highest value - twice same value`() {
        val cards = setOf(Card.Clubs3, Card.Diamonds10, Card.SpadesAce, Card.Hearts5, Card.HeartsAce)

        val cardExchange = CardExchangeComputer.getCardExchange(playerId, DwitchRank.Asshole, cards)

        assertThat(cardExchange!!.allowedCardValues).containsExactlyInAnyOrder(CardName.Ace, CardName.Ace)
    }

    @Test
    fun `Asshole must choose the 2 cards with the highest value - different values`() {
        val cards = setOf(Card.Clubs3, Card.Diamonds10, Card.SpadesAce, Card.Hearts5)

        val cardExchange = CardExchangeComputer.getCardExchange(playerId, DwitchRank.Asshole, cards)

        assertThat(cardExchange!!.allowedCardValues).containsExactlyInAnyOrder(CardName.Ten, CardName.Ace)
    }

    @Test
    fun `Asshole must choose the jokers when it has some (the joker has the highest value) - two jokers`() {
        val cards = setOf(Card.Clubs2, Card.Spades2, Card.Clubs3, Card.Diamonds10, Card.SpadesAce, Card.Hearts5)

        val cardExchange = CardExchangeComputer.getCardExchange(playerId, DwitchRank.Asshole, cards)

        assertThat(cardExchange!!.allowedCardValues).containsExactlyInAnyOrder(INITIAL_JOKER, INITIAL_JOKER)
    }

    @Test
    fun `Asshole must choose the jokers when it has some (the joker has the highest value) - one joker`() {
        val cards = setOf(Card.Clubs2, Card.Clubs3, Card.Diamonds10, Card.SpadesAce, Card.Hearts5)

        val cardExchange = CardExchangeComputer.getCardExchange(playerId, DwitchRank.Asshole, cards)

        assertThat(cardExchange!!.allowedCardValues).containsExactlyInAnyOrder(INITIAL_JOKER, CardName.Ace)
    }

    @Test
    fun `Vice-Asshole must choose the card with the highest value`() {
        val cards = setOf(Card.Clubs3, Card.Diamonds10, Card.SpadesAce, Card.Hearts5, Card.HeartsAce)

        val cardExchange = CardExchangeComputer.getCardExchange(playerId, DwitchRank.ViceAsshole, cards)

        assertThat(cardExchange!!.allowedCardValues).containsExactlyInAnyOrder(CardName.Ace)
    }

    @Test
    fun `Vice-Asshole must choose the joker when it has some (the joker has the highest value)`() {
        val cards = setOf(Card.Clubs2, Card.Spades2, Card.Clubs3, Card.Diamonds10, Card.SpadesAce, Card.Hearts5)

        val cardExchange = CardExchangeComputer.getCardExchange(playerId, DwitchRank.ViceAsshole, cards)

        assertThat(cardExchange!!.allowedCardValues).containsExactlyInAnyOrder(INITIAL_JOKER)
    }
}
