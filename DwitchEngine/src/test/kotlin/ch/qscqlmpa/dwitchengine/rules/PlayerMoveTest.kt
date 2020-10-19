package ch.qscqlmpa.dwitchengine.rules

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class PlayerMoveTest {

    @Nested
    @DisplayName("cardPlayedIsAValidMove")
    inner class CardPlayedIsAValidMove {

        @Test
        fun `Card played has higher value than the last card on table and hence is a valid move`() {
            val lastCardOnTable = Card.Clubs5
            val cardPlayed = Card.Diamonds7
            val joker = CardName.Two

            val result = PlayerMove.cardPlayedIsAValidMove(lastCardOnTable, cardPlayed, joker)
            assertThat(result).isEqualTo(true)
        }

        @Test
        fun `Card played has the same value as the last card on table and hence is a valid move`() {
            val lastCardOnTable = Card.Clubs5
            val cardPlayed = Card.Diamonds5
            val joker = CardName.Two

            val result = PlayerMove.cardPlayedIsAValidMove(lastCardOnTable, cardPlayed, joker)
            assertThat(result).isEqualTo(true)
        }

        @Test
        fun `Card played is joker and hence is a valid move`() {
            val lastCardOnTable = Card.Clubs5
            val cardPlayed = Card.Diamonds2
            val joker = CardName.Two

            val result = PlayerMove.cardPlayedIsAValidMove(lastCardOnTable, cardPlayed, joker)
            assertThat(result).isEqualTo(true)
        }

        @Test
        fun `There is no last card on table and hence is a valid move`() {
            val lastCardOnTable = null
            val cardPlayed = Card.Diamonds3
            val joker = CardName.Two

            val result = PlayerMove.cardPlayedIsAValidMove(lastCardOnTable, cardPlayed, joker)
            assertThat(result).isEqualTo(true)
        }

        @Test
        fun `Card played has lower value than last card on table and hence is not a valid move`() {
            val lastCardOnTable = Card.Clubs7
            val cardPlayed = Card.Diamonds5
            val joker = CardName.Two

            val result = PlayerMove.cardPlayedIsAValidMove(lastCardOnTable, cardPlayed, joker)
            assertThat(result).isEqualTo(false)
        }
    }
}