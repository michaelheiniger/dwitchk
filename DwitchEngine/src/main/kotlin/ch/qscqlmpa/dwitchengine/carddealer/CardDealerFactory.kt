package ch.qscqlmpa.dwitchengine.carddealer

import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId

interface CardDealerFactory {

    fun getCardDealer(playersId: Set<DwitchPlayerId>): CardDealer
}
