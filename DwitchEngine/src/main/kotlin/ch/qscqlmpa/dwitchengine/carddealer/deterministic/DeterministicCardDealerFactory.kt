package ch.qscqlmpa.dwitchengine.carddealer.deterministic

import ch.qscqlmpa.dwitchengine.carddealer.CardDealer
import ch.qscqlmpa.dwitchengine.carddealer.CardDealerFactory
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId

class DeterministicCardDealerFactory(cardDealer: CardDealer?) : CardDealerFactory {

    constructor() : this(null)

    private var instance: CardDealer? = cardDealer

    fun setInstance(cardDealer: CardDealer): DeterministicCardDealerFactory {
        instance = cardDealer
        return this
    }

    override fun getCardDealer(playersId: Set<DwitchPlayerId>): CardDealer {
        val instanceToReturn = instance
        instance = null
        return instanceToReturn
            ?: throw IllegalStateException("No instance initialized in the factory.")
    }
}
