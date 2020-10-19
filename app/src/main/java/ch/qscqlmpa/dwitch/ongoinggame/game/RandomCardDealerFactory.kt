package ch.qscqlmpa.dwitch.ongoinggame.game

import ch.qscqlmpa.dwitchengine.CardDealer
import ch.qscqlmpa.dwitchengine.CardDealerFactory
import javax.inject.Inject

class RandomCardDealerFactory @Inject constructor() : CardDealerFactory {

    override fun getCardDealer(numPlayers: Int): CardDealer {
        return RandomCardDealer(numPlayers)
    }
}