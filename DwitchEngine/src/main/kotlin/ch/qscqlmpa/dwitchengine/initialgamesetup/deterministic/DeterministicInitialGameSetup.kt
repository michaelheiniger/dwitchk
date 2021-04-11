package ch.qscqlmpa.dwitchengine.initialgamesetup.deterministic

import ch.qscqlmpa.dwitchengine.carddealer.deterministic.DeterministicCardDealer
import ch.qscqlmpa.dwitchengine.initialgamesetup.InitialGameSetup
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank

class DeterministicInitialGameSetup(
    cardsForPlayer: Map<Int, List<Card>>,
    private val rankForPlayer: Map<Int, DwitchRank>
) : InitialGameSetup(cardsForPlayer.size) {

    private val cardDealer = DeterministicCardDealer(cardsForPlayer)

    override fun getCardsForPlayer(index: Int): List<Card> {
        return cardDealer.getCardsForPlayer(index)
    }

    override fun getRemainingCards(): List<Card> {
        return cardDealer.getRemainingCards()
    }

    override fun getRankForPlayer(index: Int): DwitchRank {
        return rankForPlayer[index] ?: error("No rank for index $index")
    }
}
