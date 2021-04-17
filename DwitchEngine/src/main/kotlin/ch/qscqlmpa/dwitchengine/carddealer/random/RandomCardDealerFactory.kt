package ch.qscqlmpa.dwitchengine.carddealer.random

import ch.qscqlmpa.dwitchengine.carddealer.CardDealer
import ch.qscqlmpa.dwitchengine.carddealer.CardDealerFactory
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId

class RandomCardDealerFactory : CardDealerFactory {

    override fun getCardDealer(playersId: Set<DwitchPlayerId>): CardDealer {
        return RandomCardDealer(playersId)
    }
}
