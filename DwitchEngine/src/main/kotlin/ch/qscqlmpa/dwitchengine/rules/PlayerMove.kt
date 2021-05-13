package ch.qscqlmpa.dwitchengine.rules

import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.game.PlayedCards

internal object PlayerMove {

    fun cardPlayedIsAValidMove(lastCardOnTable: PlayedCards?, playedCards: PlayedCards, joker: CardName): Boolean {
        return lastCardOnTable == null || // Then any card can be played
                (
                        lastCardOnTable.multiplicity == playedCards.multiplicity &&
                                (playedCards.value >= lastCardOnTable.value || playedCards.name == joker)
                        )
    }
}
