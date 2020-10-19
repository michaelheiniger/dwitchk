package ch.qscqlmpa.dwitchengine

class TestCardDealerFactory : CardDealerFactory {

    private lateinit var cardDealer: TestCardDealer

    override fun getCardDealer(numPlayers: Int): CardDealer {
        return cardDealer
    }

    fun setCardDealer(cardDealer: TestCardDealer) {
        this.cardDealer = cardDealer
    }
}