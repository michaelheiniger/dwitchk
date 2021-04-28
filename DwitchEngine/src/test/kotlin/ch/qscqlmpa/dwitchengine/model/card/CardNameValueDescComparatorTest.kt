package ch.qscqlmpa.dwitchengine.model.card

import ch.qscqlmpa.dwitchengine.model.info.DwitchCardInfo
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class CardNameValueDescComparatorTest {

    @Nested
    inner class CardNameValueComparator {

        @Test
        fun `CardNameValueDescComparator sorts items on their card value DESC with the joker having the highest value`() {
            // Given
            val unsortedCards = listOf(CardName.Four, CardName.Two, CardName.Three, CardName.Ten)

            // When
            val sortedCards = unsortedCards.sortedWith(CardNameValueDescComparator(CardName.Two))

            // Then
            assertThat(sortedCards).containsExactly(CardName.Two, CardName.Ten, CardName.Four, CardName.Three)
        }
    }


    @Nested
    inner class CardValueComparator {

        @Test
        fun `CardValueDescComparator sorts items on their card value DESC with the joker having the highest value`() {
            // Given
            val unsortedCards = listOf(Card.Diamonds4, Card.Clubs2, Card.Spades3, Card.Hearts10)

            // When
            val sortedCards = unsortedCards.sortedWith(CardValueDescComparator(CardName.Two))

            // Then
            assertThat(sortedCards).containsExactly(Card.Clubs2, Card.Hearts10, Card.Diamonds4, Card.Spades3)
        }

        @Test
        fun `CardValueAscComparator sorts items on their card value ASC with the joker having the highest value`() {
            // Given
            val unsortedCards = listOf(Card.Clubs2, Card.Diamonds4, Card.Spades3, Card.Hearts10)

            // When
            val sortedCards = unsortedCards.sortedWith(CardValueAscComparator(CardName.Two))

            // Then
            assertThat(sortedCards).containsExactly(Card.Spades3, Card.Diamonds4, Card.Hearts10, Card.Clubs2)
        }
    }

    @Nested
    inner class DwitchCardInfoValueComparator {

        @Test
        fun `DwitchCardInfoValueDescComparator sorts items on their card value DESC with the joker having the highest value`() {
            // Given
            val unsortedCards = listOf(
                DwitchCardInfo(Card.Diamonds4),
                DwitchCardInfo(Card.Clubs2),
                DwitchCardInfo(Card.Spades3),
                DwitchCardInfo(Card.Hearts10)
            )

            // When
            val sortedCards = unsortedCards.sortedWith(DwitchCardInfoValueDescComparator(CardName.Two))

            // Then
            assertThat(sortedCards).containsExactly(
                DwitchCardInfo(Card.Clubs2),
                DwitchCardInfo(Card.Hearts10),
                DwitchCardInfo(Card.Diamonds4),
                DwitchCardInfo(Card.Spades3)
            )
        }

        @Test
        fun `DwitchCardInfoValueAscComparator sorts items on their card value ASC with the joker having the highest value`() {
            // Given
            val unsortedCards = listOf(
                DwitchCardInfo(Card.Clubs2),
                DwitchCardInfo(Card.Diamonds4),
                DwitchCardInfo(Card.Spades3),
                DwitchCardInfo(Card.Hearts10)
            )

            // When
            val sortedCards = unsortedCards.sortedWith(DwitchCardInfoValueAscComparator(CardName.Two))

            // Then
            assertThat(sortedCards).containsExactly(
                DwitchCardInfo(Card.Spades3),
                DwitchCardInfo(Card.Diamonds4),
                DwitchCardInfo(Card.Hearts10),
                DwitchCardInfo(Card.Clubs2)
            )
        }
    }
}