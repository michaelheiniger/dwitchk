package ch.qscqlmpa.dwitchengine.rules

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.game.PlayedCards
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class PlayerMoveTest {

    @Nested
    inner class CardPlayedIsAValidMove {

        @Test
        fun `Card played has higher value than the last card on table and hence is a valid move`() {
            val lastCardOnTable = PlayedCards(Card.Clubs5)
            val cardPlayed = PlayedCards(Card.Diamonds7)
            val joker = CardName.Two

            val result = PlayerMove.cardPlayedIsAValidMove(lastCardOnTable, cardPlayed, joker)
            assertThat(result).isTrue
        }

        @Test
        fun `Card played has the same value as the last card on table and hence is a valid move`() {
            val lastCardOnTable = PlayedCards(Card.Clubs5)
            val cardPlayed = PlayedCards(Card.Diamonds5)
            val joker = CardName.Two

            val result = PlayerMove.cardPlayedIsAValidMove(lastCardOnTable, cardPlayed, joker)
            assertThat(result).isTrue
        }

        @Test
        fun `Card played is joker and hence is a valid move`() {
            val lastCardOnTable = PlayedCards(Card.Clubs5)
            val cardPlayed = PlayedCards(Card.Diamonds2)
            val joker = CardName.Two

            val result = PlayerMove.cardPlayedIsAValidMove(lastCardOnTable, cardPlayed, joker)
            assertThat(result).isTrue
        }

        @Test
        fun `There is no last card on table and hence is a valid move - one card played`() {
            val lastCardOnTable = null
            val cardPlayed = PlayedCards(Card.Diamonds3)
            val joker = CardName.Two

            val result = PlayerMove.cardPlayedIsAValidMove(lastCardOnTable, cardPlayed, joker)
            assertThat(result).isTrue
        }

        @Test
        fun `There is no last card on table and hence is a valid move - two cards played`() {
            val lastCardOnTable = null
            val cardPlayed = PlayedCards(Card.Diamonds3, Card.Clubs3)
            val joker = CardName.Two

            val result = PlayerMove.cardPlayedIsAValidMove(lastCardOnTable, cardPlayed, joker)
            assertThat(result).isTrue
        }

        @Test
        fun `Card played has lower value than last card on table and hence is not a valid move - one card played`() {
            val lastCardOnTable = PlayedCards(Card.Clubs7)
            val cardPlayed = PlayedCards(Card.Diamonds5)
            val joker = CardName.Two

            val result = PlayerMove.cardPlayedIsAValidMove(lastCardOnTable, cardPlayed, joker)
            assertThat(result).isFalse
        }

        @Test
        fun `Card played has lower value than last card on table and hence is not a valid move - two cards played`() {
            val lastCardOnTable = PlayedCards(Card.Clubs7, Card.Diamonds7)
            val cardPlayed = PlayedCards(Card.Diamonds5, Card.Hearts5)
            val joker = CardName.Two

            val result = PlayerMove.cardPlayedIsAValidMove(lastCardOnTable, cardPlayed, joker)
            assertThat(result).isFalse
        }

        @Test
        fun `Fewer cards played than last card on table and hence is not a valid move`() {
            val lastCardOnTable = PlayedCards(Card.Clubs3, Card.Diamonds3)
            val cardPlayed = PlayedCards(Card.Diamonds5)
            val joker = CardName.Two

            val result = PlayerMove.cardPlayedIsAValidMove(lastCardOnTable, cardPlayed, joker)
            assertThat(result).isFalse
        }

        @Test
        fun `More cards played than last card on table and hence is not a valid move`() {
            val lastCardOnTable = PlayedCards(Card.Clubs3)
            val cardPlayed = PlayedCards(Card.Diamonds5, Card.Clubs5)
            val joker = CardName.Two

            val result = PlayerMove.cardPlayedIsAValidMove(lastCardOnTable, cardPlayed, joker)
            assertThat(result).isFalse
        }

        @Test
        fun `Fewer cards played than last card on table and hence is not a valid move - joker`() {
            val lastCardOnTable = PlayedCards(Card.Clubs3, Card.Diamonds3)
            val cardPlayed = PlayedCards(Card.Diamonds2)
            val joker = CardName.Two

            val result = PlayerMove.cardPlayedIsAValidMove(lastCardOnTable, cardPlayed, joker)
            assertThat(result).isFalse
        }

        @Test
        fun `More cards played than last card on table and hence is not a valid move - joker`() {
            val lastCardOnTable = PlayedCards(Card.Clubs3)
            val cardPlayed = PlayedCards(Card.Diamonds2, Card.Clubs2)
            val joker = CardName.Two

            val result = PlayerMove.cardPlayedIsAValidMove(lastCardOnTable, cardPlayed, joker)
            assertThat(result).isFalse
        }
    }
}
