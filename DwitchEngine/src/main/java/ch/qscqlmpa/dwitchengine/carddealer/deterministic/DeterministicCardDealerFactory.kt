package ch.qscqlmpa.dwitchengine.carddealer.deterministic

import ch.qscqlmpa.dwitchengine.carddealer.CardDealer
import ch.qscqlmpa.dwitchengine.carddealer.CardDealerFactory

class DeterministicCardDealerFactory : CardDealerFactory {

    private lateinit var cardDealer: DeterministicCardDealer

    override fun getCardDealer(numPlayers: Int): CardDealer {
        return cardDealer
    }

    fun setCardDealer(cardDealer: DeterministicCardDealer) {
        this.cardDealer = cardDealer
    }
}