package ch.qscqlmpa.dwitchengine.initialgamesetup.random

import ch.qscqlmpa.dwitchengine.carddealer.random.RandomCardDealer
import ch.qscqlmpa.dwitchengine.initialgamesetup.InitialGameSetup
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank

class RandomInitialGameSetup(private val playersId: Set<DwitchPlayerId>) : InitialGameSetup(playersId) {

    private val cardDealer = RandomCardDealer(playersId)
    private val ranksAssignment: Map<DwitchPlayerId, DwitchRank> = assignInitialRanksRandomly()

    override fun getCardsForPlayer(id: DwitchPlayerId): Set<Card> {
        return cardDealer.getCardsForPlayer(id)
    }

    override fun getRemainingCards(): Set<Card> {
        return cardDealer.getRemainingCards()
    }

    override fun getRankForPlayer(id: DwitchPlayerId): DwitchRank {
        checkId(id)
        return ranksAssignment.getValue(id)
    }

    private fun assignInitialRanksRandomly(): Map<DwitchPlayerId, DwitchRank> {
        val ranksToAssign = when (numPlayers) {
            2 -> listOf(DwitchRank.President, DwitchRank.Asshole)
            3 -> listOf(DwitchRank.President, DwitchRank.Neutral, DwitchRank.Asshole)
            4 -> listOf(DwitchRank.President, DwitchRank.VicePresident, DwitchRank.ViceAsshole, DwitchRank.Asshole)
            else -> {
                val ranks = MutableList<DwitchRank>(numPlayers - 4) { DwitchRank.Neutral }
                ranks.addAll(listOf(DwitchRank.President, DwitchRank.VicePresident, DwitchRank.ViceAsshole, DwitchRank.Asshole))
                ranks
            }
        }.shuffled()
        return playersId.mapIndexed { index, playerId -> playerId to ranksToAssign[index] }.toMap()
    }
}
