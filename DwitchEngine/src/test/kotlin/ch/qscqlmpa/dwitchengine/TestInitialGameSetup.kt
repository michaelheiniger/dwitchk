package ch.qscqlmpa.dwitchengine

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.player.Rank

class TestInitialGameSetup(private val numPlayers: Int) : InitialGameSetup(numPlayers) {

    private lateinit var cardDealer: TestCardDealer
    private lateinit var rankForPlayer: Map<Int, Rank>

    fun initialize(cardsForPlayer: Map<Int, List<Card>>, rankForPlayer: Map<Int, Rank>) {
        cardDealer = TestCardDealer(numPlayers, cardsForPlayer)
        this.rankForPlayer = rankForPlayer
    }

    override fun getCardsForPlayer(index: Int): List<Card> {
        return cardDealer.getCardsForPlayer(index)
    }

    override fun getRemainingCards(): List<Card> {
        return cardDealer.getRemainingCards()
    }

    override fun getRankForPlayer(index: Int): Rank {
        return rankForPlayer[index] ?: error("No rank for index $index")
    }
}