package ch.qscqlmpa.dwitchengine.rules

import ch.qscqlmpa.dwitchengine.model.card.Card
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class SpecialRuleTest {

    @Nested
    inner class FirstJackOfTheRound {

        @Test
        fun `last card played is the first jack played in the round`() {
            val cardsOnTable = listOf(Card.Hearts5, Card.SpadesJack)
            val cardsInGraveyard = listOf(Card.ClubsAce, Card.Diamonds3)

            val result = SpecialRule.isLastCardPlayedTheFirstJackOfTheRound(cardsOnTable, cardsInGraveyard)

            assertThat(result).isTrue
        }

        @Test
        fun `last card played is the second jack played in the round`() {
            val cardsOnTable = listOf(Card.Hearts5, Card.SpadesJack)
            val cardsInGraveyard = listOf(Card.ClubsJack, Card.Diamonds3)

            val result = SpecialRule.isLastCardPlayedTheFirstJackOfTheRound(cardsOnTable, cardsInGraveyard)

            assertThat(result).isFalse
        }

        @Test
        fun `last card played is not a jack`() {
            val cardsOnTable = listOf(Card.Hearts5, Card.Spades10)
            val cardsInGraveyard = listOf(Card.ClubsJack, Card.DiamondsJack)

            val result = SpecialRule.isLastCardPlayedTheFirstJackOfTheRound(cardsOnTable, cardsInGraveyard)

            assertThat(result).isFalse
        }

        @Test
        fun `last card is null`() {
            val cardsOnTable = emptyList<Card>()
            val cardsInGraveyard = listOf(Card.ClubsJack, Card.DiamondsJack)

            val result = SpecialRule.isLastCardPlayedTheFirstJackOfTheRound(cardsOnTable, cardsInGraveyard)

            assertThat(result).isFalse
        }
    }
}