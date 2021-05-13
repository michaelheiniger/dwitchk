package ch.qscqlmpa.dwitchengine.rules

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.PlayedCards
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SpecialRuleTest {

    @Nested
    inner class FirstJackOfTheRound {

        @Test
        fun `last card played is the first jack played in the round`() {
            val cardsOnTable = listOf(PlayedCards(Card.Hearts5), PlayedCards(Card.SpadesJack))
            val cardsInGraveyard = listOf(PlayedCards(Card.ClubsAce), PlayedCards(Card.Diamonds3))

            val result = SpecialRule.isLastCardPlayedTheFirstJackOfTheRound(cardsOnTable, cardsInGraveyard)

            assertThat(result).isTrue
        }

        @Test
        fun `last card played is the second jack played in the round`() {
            val cardsOnTable = listOf(PlayedCards(Card.Hearts5), PlayedCards(Card.SpadesJack))
            val cardsInGraveyard = listOf(PlayedCards(Card.ClubsJack), PlayedCards(Card.Diamonds3))

            val result = SpecialRule.isLastCardPlayedTheFirstJackOfTheRound(cardsOnTable, cardsInGraveyard)

            assertThat(result).isFalse
        }

        @Test
        fun `last card played is not a jack`() {
            val cardsOnTable = listOf(PlayedCards(Card.Hearts5), PlayedCards(Card.Spades10))
            val cardsInGraveyard = listOf(PlayedCards(Card.ClubsJack), PlayedCards(Card.DiamondsJack))

            val result = SpecialRule.isLastCardPlayedTheFirstJackOfTheRound(cardsOnTable, cardsInGraveyard)

            assertThat(result).isFalse
        }

        @Test
        fun `last card is null`() {
            val cardsOnTable = emptyList<PlayedCards>()
            val cardsInGraveyard = listOf(PlayedCards(Card.ClubsJack), PlayedCards(Card.DiamondsJack))

            val result = SpecialRule.isLastCardPlayedTheFirstJackOfTheRound(cardsOnTable, cardsInGraveyard)

            assertThat(result).isFalse
        }
    }
}