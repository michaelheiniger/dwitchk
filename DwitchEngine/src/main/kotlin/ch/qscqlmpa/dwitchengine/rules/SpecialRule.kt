package ch.qscqlmpa.dwitchengine.rules

import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.game.PlayedCards

object SpecialRule {

    fun isLastCardPlayedTheFirstJackOfTheRound(cardsOnTable: List<PlayedCards>, cardsInGraveyard: List<PlayedCards>): Boolean {
        val lastCard = cardsOnTable.lastOrNull()
        return lastCard != null &&
                lastCard.name == CardName.Jack &&
                cardsInGraveyard.none { c -> c.name == CardName.Jack }
    }
}