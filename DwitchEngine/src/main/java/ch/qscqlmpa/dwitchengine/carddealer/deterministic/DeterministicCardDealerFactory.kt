package ch.qscqlmpa.dwitchengine.carddealer.deterministic

import ch.qscqlmpa.dwitchengine.carddealer.CardDealer
import ch.qscqlmpa.dwitchengine.carddealer.CardDealerFactory

class DeterministicCardDealerFactory(cardDealer: CardDealer?) : CardDealerFactory {

    constructor(): this(null)

    private var instance: CardDealer? = cardDealer

    fun setInstance(cardDealer: CardDealer) {
        instance = cardDealer
    }

    override fun getCardDealer(numPlayers: Int): CardDealer {
        val instanceToReturn = instance
        instance = null
        return instanceToReturn
            ?: throw IllegalStateException("No instance initialized in the factory.")
    }
}