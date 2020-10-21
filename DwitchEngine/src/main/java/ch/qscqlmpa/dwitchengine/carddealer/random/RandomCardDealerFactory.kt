package ch.qscqlmpa.dwitchengine.carddealer.random

import ch.qscqlmpa.dwitchengine.carddealer.CardDealer
import ch.qscqlmpa.dwitchengine.carddealer.CardDealerFactory

class RandomCardDealerFactory : CardDealerFactory {

    override fun getCardDealer(numPlayers: Int): CardDealer {
        return RandomCardDealer(numPlayers)
    }
}