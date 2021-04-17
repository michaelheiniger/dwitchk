package ch.qscqlmpa.dwitchengine.carddealer.deterministic

import ch.qscqlmpa.dwitchengine.carddealer.CardDealer
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardUtil
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId

class DeterministicCardDealer(
    private val cardsForPlayer: Map<DwitchPlayerId, Set<Card>>
) : CardDealer(cardsForPlayer.keys) {

    private val _remainingCards: Set<Card> by lazy {
        CardUtil.getAllCardsExcept(cardsForPlayer.map { entry -> entry.value }.flatten()).toSet()
    }

    override fun getCardsForPlayer(id: DwitchPlayerId): Set<Card> {
        return cardsForPlayer.getValue(id)
    }

    override fun getRemainingCards(): Set<Card> {
        return _remainingCards
    }
}
