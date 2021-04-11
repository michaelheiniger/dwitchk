package ch.qscqlmpa.dwitchengine.rules

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName

internal object PlayerMove {

    fun cardPlayedIsAValidMove(lastCardOnTable: Card?, playedCard: Card, joker: CardName): Boolean {
        return lastCardOnTable == null || // Then any card can be played
            playedCard.value() >= lastCardOnTable.value() ||
            playedCard.name == joker
    }
}
