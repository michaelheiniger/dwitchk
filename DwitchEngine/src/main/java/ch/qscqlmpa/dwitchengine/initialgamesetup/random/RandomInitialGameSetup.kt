package ch.qscqlmpa.dwitchengine.initialgamesetup.random

import ch.qscqlmpa.dwitchengine.carddealer.random.RandomCardDealer
import ch.qscqlmpa.dwitchengine.initialgamesetup.InitialGameSetup
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank

class RandomInitialGameSetup(private val numPlayers: Int) : InitialGameSetup(numPlayers) {

    private val cardDealer = RandomCardDealer(numPlayers)
    private val ranks: List<DwitchRank> = computeInitialRanks().shuffled()

    override fun getCardsForPlayer(index: Int): List<Card> {
        checkIndex(index)
        return cardDealer.getCardsForPlayer(index)
    }

    override fun getRemainingCards(): List<Card> {
        return cardDealer.getRemainingCards()
    }

    override fun getRankForPlayer(index: Int): DwitchRank {
        checkIndex(index)
        return ranks[index]
    }

    private fun computeInitialRanks(): List<DwitchRank> {
        return when (numPlayers) {
            2 -> listOf(DwitchRank.President, DwitchRank.Asshole)
            3 -> listOf(DwitchRank.President, DwitchRank.Neutral, DwitchRank.Asshole)
            4 -> listOf(DwitchRank.President, DwitchRank.VicePresident, DwitchRank.ViceAsshole, DwitchRank.Asshole)
            else -> {
                val ranks = MutableList<DwitchRank>(numPlayers - 4) { DwitchRank.Neutral }
                ranks.addAll(listOf(DwitchRank.President, DwitchRank.VicePresident, DwitchRank.ViceAsshole, DwitchRank.Asshole))
                return ranks
            }
        }
    }
}
