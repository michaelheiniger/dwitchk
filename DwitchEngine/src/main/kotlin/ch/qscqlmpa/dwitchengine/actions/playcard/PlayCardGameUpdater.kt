package ch.qscqlmpa.dwitchengine.actions.playcard

import ch.qscqlmpa.dwitchengine.actions.GameUpdaterBase
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameEvent
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId

internal class PlayCardGameUpdater(
    currentGameState: DwitchGameState
) : GameUpdaterBase(currentGameState) {

    fun clearTable(cardPlayed: Card) {
        gameStateMutable.moveCardsFromTableToGraveyard()
        gameStateMutable.dwitchGameEvent = DwitchGameEvent.TableHasBeenCleared(cardPlayed)
    }

    fun takeCardFromHandAndPutOnTable(playerId: DwitchPlayerId, cardPlayed: Card) {
        gameStateMutable.removeCardFromHand(playerId, cardPlayed)
        gameStateMutable.addCardToTable(cardPlayed)
    }

    fun dwitchPlayer(playerId: DwitchPlayerId) {
        gameStateMutable.dwitchPlayer(playerId)
    }
}
