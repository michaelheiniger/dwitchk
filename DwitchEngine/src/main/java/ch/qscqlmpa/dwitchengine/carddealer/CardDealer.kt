package ch.qscqlmpa.dwitchengine.carddealer

import ch.qscqlmpa.dwitchengine.model.card.Card

abstract class CardDealer(private val numPlayers: Int) {

    abstract fun getCardsForPlayer(index: Int): List<Card>

    abstract fun getRemainingCards(): List<Card>

    protected fun checkIndex(index: Int) {
        if (index < 0 || index >= numPlayers) {
            throw IllegalArgumentException("Argument index $index must be in {0, 1, ..., ${numPlayers - 1}}")
        }
    }
}
