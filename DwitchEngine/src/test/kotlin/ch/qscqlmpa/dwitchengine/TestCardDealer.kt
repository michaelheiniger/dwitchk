package ch.qscqlmpa.dwitchengine

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardUtil

class TestCardDealer(numPlayers: Int, private val cardsForPlayer: Map<Int, List<Card>>) : CardDealer(numPlayers) {

    private val remainingCards: List<Card>

    init {
        this.remainingCards = CardUtil.getAllCardsExcept(cardsForPlayer.map { entry -> entry.value }.flatten())
    }

    override fun getCardsForPlayer(index: Int): List<Card> {
        return cardsForPlayer[index] ?: error("No card for in-game ID $index")
    }

    override fun getRemainingCards(): List<Card> {
        return remainingCards
    }
}