package ch.qscqlmpa.dwitchengine

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.player.Rank

internal class RandomInitialGameSetup(private val numPlayers: Int) : InitialGameSetup(numPlayers) {

    private val cardDealer = RandomCardDealer(numPlayers)
    private val ranks: List<Rank> = computeInitialRanks().shuffled()

    override fun getCardsForPlayer(index: Int): List<Card> {
        checkIndex(index)
        return cardDealer.getCardsForPlayer(index)
    }

    override fun getRemainingCards(): List<Card> {
        return cardDealer.getRemainingCards()
    }

    override fun getRankForPlayer(index: Int): Rank {
        checkIndex(index)
        return ranks[index]
    }

    private fun computeInitialRanks(): List<Rank> {
        return when (numPlayers) {
            2 -> listOf(Rank.President, Rank.Asshole)
            3 -> listOf(Rank.President, Rank.Neutral, Rank.Asshole)
            4 -> listOf(Rank.President, Rank.VicePresident, Rank.ViceAsshole, Rank.Asshole)
            else -> {
                val ranks = MutableList<Rank>(numPlayers - 4) { Rank.Neutral }
                ranks.addAll(listOf(Rank.President, Rank.VicePresident, Rank.ViceAsshole, Rank.Asshole))
                return ranks
            }
        }
    }
}