package ch.qscqlmpa.dwitchengine.initialgamesetup.deterministic

import ch.qscqlmpa.dwitchengine.carddealer.deterministic.DeterministicCardDealer
import ch.qscqlmpa.dwitchengine.initialgamesetup.InitialGameSetup
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.player.Rank

class DeterministicInitialGameSetup(private val numPlayers: Int) : InitialGameSetup(numPlayers) {

    private lateinit var cardDealer: DeterministicCardDealer
    private lateinit var rankForPlayer: Map<Int, Rank>

    fun initialize(cardsForPlayer: Map<Int, List<Card>>, rankForPlayer: Map<Int, Rank>) {
        cardDealer = DeterministicCardDealer(numPlayers, cardsForPlayer)
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