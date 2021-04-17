package ch.qscqlmpa.dwitchengine.carddealer.random

import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchengine.utils.CollectionUtil.mergeWith
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test

class RandomCardDealerTest {

    private val player1 = DwitchPlayerId(1)
    private val player2 = DwitchPlayerId(2)
    private val player3 = DwitchPlayerId(3)
    private val player4 = DwitchPlayerId(4)
    private val player5 = DwitchPlayerId(5)
    private val player6 = DwitchPlayerId(6)
    private val player7 = DwitchPlayerId(7)
    private val player8 = DwitchPlayerId(8)

    @Test
    fun `All cards are equally split among players when there are 2 players`() {
        val cardDealer = RandomCardDealer(setOf(player1, player2))
        val cardsPerPlayer = 52 / 2 // 26

        val cardsPlayer1 = cardDealer.getCardsForPlayer(player1)
        val cardsPlayer2 = cardDealer.getCardsForPlayer(player2)

        assertThat(cardsPlayer1.size).isEqualTo(cardsPerPlayer)
        assertThat(cardsPlayer2.size).isEqualTo(cardsPerPlayer)
        assertThat(cardDealer.getRemainingCards().size).isEqualTo(0)
        assertThat(cardsPlayer1.mergeWith(cardsPlayer2).size).isEqualTo(52)
    }

    @Test
    fun `All cards one 3 are equally split among players when there are 3 players`() {
        val cardDealer = RandomCardDealer(setOf(player1, player2, player3))
        val cardsPerPlayer = (52 - 1) / 3 // 17

        val cardsPlayer1 = cardDealer.getCardsForPlayer(player1)
        val cardsPlayer2 = cardDealer.getCardsForPlayer(player2)
        val cardsPlayer3 = cardDealer.getCardsForPlayer(player3)
        val remainingCards = cardDealer.getRemainingCards()

        assertThat(cardsPlayer1.size).isEqualTo(cardsPerPlayer)
        assertThat(cardsPlayer2.size).isEqualTo(cardsPerPlayer)
        assertThat(cardsPlayer3.size).isEqualTo(cardsPerPlayer)
        assertThat(remainingCards.filter { c -> c.value() == 3 }.size).isEqualTo(1)
        assertThat(cardsPlayer1.mergeWith(cardsPlayer2, cardsPlayer3).size).isEqualTo(52 - 1)
    }

    @Test
    fun `All cards are equally split among players when there are 4 players`() {
        val cardDealer = RandomCardDealer(setOf(player1, player2, player3, player4))
        val cardsPerPlayer = 52 / 4 // 13

        val cardsPlayer1 = cardDealer.getCardsForPlayer(player1)
        val cardsPlayer2 = cardDealer.getCardsForPlayer(player2)
        val cardsPlayer3 = cardDealer.getCardsForPlayer(player3)
        val cardsPlayer4 = cardDealer.getCardsForPlayer(player4)

        assertThat(cardsPlayer1.size).isEqualTo(cardsPerPlayer)
        assertThat(cardsPlayer2.size).isEqualTo(cardsPerPlayer)
        assertThat(cardsPlayer3.size).isEqualTo(cardsPerPlayer)
        assertThat(cardsPlayer4.size).isEqualTo(cardsPerPlayer)
        assertThat(cardDealer.getRemainingCards().size).isEqualTo(0)
        assertThat(cardsPlayer1.mergeWith(cardsPlayer2, cardsPlayer3, cardsPlayer4).size).isEqualTo(52)
    }

    @Test
    fun `All cards except two 3s are equally split among players when there are 5 players`() {
        val cardDealer = RandomCardDealer(setOf(player1, player2, player3, player4, player5))
        val cardsPerPlayer = (52 - 2) / 5 // 10

        val cardsPlayer1 = cardDealer.getCardsForPlayer(player1)
        val cardsPlayer2 = cardDealer.getCardsForPlayer(player2)
        val cardsPlayer3 = cardDealer.getCardsForPlayer(player3)
        val cardsPlayer4 = cardDealer.getCardsForPlayer(player4)
        val cardsPlayer5 = cardDealer.getCardsForPlayer(player5)
        val remainingCards = cardDealer.getRemainingCards()

        assertThat(cardsPlayer1.size).isEqualTo(cardsPerPlayer)
        assertThat(cardsPlayer2.size).isEqualTo(cardsPerPlayer)
        assertThat(cardsPlayer3.size).isEqualTo(cardsPerPlayer)
        assertThat(cardsPlayer4.size).isEqualTo(cardsPerPlayer)
        assertThat(cardsPlayer5.size).isEqualTo(cardsPerPlayer)
        assertThat(remainingCards.filter { c -> c.value() == 3 }.size).isEqualTo(2)
        assertThat(cardsPlayer1.mergeWith(cardsPlayer2, cardsPlayer3, cardsPlayer4, cardsPlayer5).size).isEqualTo(52 - 2)
    }

    @Test
    fun `All cards except all 3s are equally split among players when there are 6 players`() {
        val cardDealer = RandomCardDealer(setOf(player1, player2, player3, player4, player5, player6))
        val cardsPerPlayer = (52 - 4) / 6 // 8

        val cardsPlayer1 = cardDealer.getCardsForPlayer(player1)
        val cardsPlayer2 = cardDealer.getCardsForPlayer(player2)
        val cardsPlayer3 = cardDealer.getCardsForPlayer(player3)
        val cardsPlayer4 = cardDealer.getCardsForPlayer(player4)
        val cardsPlayer5 = cardDealer.getCardsForPlayer(player5)
        val cardsPlayer6 = cardDealer.getCardsForPlayer(player6)
        val remainingCards = cardDealer.getRemainingCards()

        assertThat(cardsPlayer1.size).isEqualTo(cardsPerPlayer)
        assertThat(cardsPlayer2.size).isEqualTo(cardsPerPlayer)
        assertThat(cardsPlayer3.size).isEqualTo(cardsPerPlayer)
        assertThat(cardsPlayer4.size).isEqualTo(cardsPerPlayer)
        assertThat(cardsPlayer5.size).isEqualTo(cardsPerPlayer)
        assertThat(cardsPlayer6.size).isEqualTo(cardsPerPlayer)
        assertThat(remainingCards.filter { c -> c.value() == 3 }.size).isEqualTo(4)
        assertThat(
            cardsPlayer1.mergeWith(cardsPlayer2, cardsPlayer3, cardsPlayer4, cardsPlayer5, cardsPlayer6).size
        ).isEqualTo(52 - 4)
    }

    @Test
    fun `All cards three 3s are equally split among players when there are 7 players`() {
        val cardDealer = RandomCardDealer(setOf(player1, player2, player3, player4, player5, player6, player7))
        val cardsPerPlayer = (52 - 3) / 7 // 6

        val cardsPlayer1 = cardDealer.getCardsForPlayer(player1)
        val cardsPlayer2 = cardDealer.getCardsForPlayer(player2)
        val cardsPlayer3 = cardDealer.getCardsForPlayer(player3)
        val cardsPlayer4 = cardDealer.getCardsForPlayer(player4)
        val cardsPlayer5 = cardDealer.getCardsForPlayer(player5)
        val cardsPlayer6 = cardDealer.getCardsForPlayer(player6)
        val cardsPlayer7 = cardDealer.getCardsForPlayer(player7)
        val remainingCards = cardDealer.getRemainingCards()

        assertThat(cardsPlayer1.size).isEqualTo(cardsPerPlayer)
        assertThat(cardsPlayer2.size).isEqualTo(cardsPerPlayer)
        assertThat(cardsPlayer3.size).isEqualTo(cardsPerPlayer)
        assertThat(cardsPlayer4.size).isEqualTo(cardsPerPlayer)
        assertThat(cardsPlayer5.size).isEqualTo(cardsPerPlayer)
        assertThat(cardsPlayer6.size).isEqualTo(cardsPerPlayer)
        assertThat(cardsPlayer7.size).isEqualTo(cardsPerPlayer)
        assertThat(remainingCards.filter { c -> c.value() == 3 }.size).isEqualTo(3)
        assertThat(
            cardsPlayer1.mergeWith(cardsPlayer2, cardsPlayer3, cardsPlayer4, cardsPlayer5, cardsPlayer6, cardsPlayer7).size
        ).isEqualTo(52 - 3)
    }

    @Test
    fun `All cards except all 3s are equally split among players when there are 8 players`() {
        val cardDealer = RandomCardDealer(setOf(player1, player2, player3, player4, player5, player6, player7, player8))
        val cardsPerPlayer = (52 - 4) / 8 // 12

        val cardsPlayer1 = cardDealer.getCardsForPlayer(player1)
        val cardsPlayer2 = cardDealer.getCardsForPlayer(player2)
        val cardsPlayer3 = cardDealer.getCardsForPlayer(player3)
        val cardsPlayer4 = cardDealer.getCardsForPlayer(player4)
        val cardsPlayer5 = cardDealer.getCardsForPlayer(player5)
        val cardsPlayer6 = cardDealer.getCardsForPlayer(player6)
        val cardsPlayer7 = cardDealer.getCardsForPlayer(player7)
        val cardsPlayer8 = cardDealer.getCardsForPlayer(player8)
        val remainingCards = cardDealer.getRemainingCards()

        assertThat(cardsPlayer1.size).isEqualTo(cardsPerPlayer)
        assertThat(cardsPlayer2.size).isEqualTo(cardsPerPlayer)
        assertThat(cardsPlayer3.size).isEqualTo(cardsPerPlayer)
        assertThat(cardsPlayer4.size).isEqualTo(cardsPerPlayer)
        assertThat(cardsPlayer5.size).isEqualTo(cardsPerPlayer)
        assertThat(cardsPlayer6.size).isEqualTo(cardsPerPlayer)
        assertThat(cardsPlayer7.size).isEqualTo(cardsPerPlayer)
        assertThat(cardsPlayer8.size).isEqualTo(cardsPerPlayer)
        assertThat(remainingCards.filter { c -> c.value() == 3 }.size).isEqualTo(4)
        assertThat(
            cardsPlayer1.mergeWith(
                cardsPlayer2,
                cardsPlayer3,
                cardsPlayer4,
                cardsPlayer5,
                cardsPlayer6,
                cardsPlayer7,
                cardsPlayer8
            ).size
        ).isEqualTo(52 - 4)
    }
}
