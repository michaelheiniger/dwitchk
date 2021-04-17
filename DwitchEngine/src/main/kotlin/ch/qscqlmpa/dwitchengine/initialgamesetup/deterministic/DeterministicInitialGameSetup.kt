package ch.qscqlmpa.dwitchengine.initialgamesetup.deterministic

import ch.qscqlmpa.dwitchengine.carddealer.deterministic.DeterministicCardDealer
import ch.qscqlmpa.dwitchengine.initialgamesetup.InitialGameSetup
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank

class DeterministicInitialGameSetup(
    cardsForPlayer: Map<DwitchPlayerId, Set<Card>>,
    private val rankForPlayer: Map<DwitchPlayerId, DwitchRank>
) : InitialGameSetup(cardsForPlayer.keys) {

    private val cardDealer = DeterministicCardDealer(cardsForPlayer)

    override fun getCardsForPlayer(id: DwitchPlayerId): Set<Card> {
        return cardDealer.getCardsForPlayer(id)
    }

    override fun getRemainingCards(): Set<Card> {
        return cardDealer.getRemainingCards()
    }

    override fun getRankForPlayer(id: DwitchPlayerId): DwitchRank {
        return rankForPlayer.getValue(id)
    }
}
