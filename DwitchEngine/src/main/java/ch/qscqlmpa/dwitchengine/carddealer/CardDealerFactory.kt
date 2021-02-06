package ch.qscqlmpa.dwitchengine.carddealer

interface CardDealerFactory {

    fun getCardDealer(numPlayers: Int): CardDealer
}
