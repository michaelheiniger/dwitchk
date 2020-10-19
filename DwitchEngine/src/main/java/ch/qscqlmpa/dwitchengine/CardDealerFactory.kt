package ch.qscqlmpa.dwitchengine

interface CardDealerFactory {

    fun getCardDealer(numPlayers: Int): CardDealer
}