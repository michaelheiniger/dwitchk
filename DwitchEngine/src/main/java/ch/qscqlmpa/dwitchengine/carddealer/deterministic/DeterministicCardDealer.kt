package ch.qscqlmpa.dwitchengine.carddealer.deterministic

import ch.qscqlmpa.dwitchengine.carddealer.CardDealer
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardUtil

class DeterministicCardDealer(
    private val cardsForPlayer: Map<Int, List<Card>>
) : CardDealer(cardsForPlayer.size) {

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
