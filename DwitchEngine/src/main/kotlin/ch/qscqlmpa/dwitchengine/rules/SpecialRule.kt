package ch.qscqlmpa.dwitchengine.rules

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName

object SpecialRule {

    fun isLastCardPlayedTheFirstJackOfTheRound(cardsOnTable: List<Card>, cardsInGraveyard: List<Card>): Boolean {
        val lastCard = cardsOnTable.lastOrNull()
        return lastCard != null &&
                lastCard.name == CardName.Jack &&
                cardsInGraveyard.none { c -> c.name == CardName.Jack }
    }
}