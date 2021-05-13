package ch.qscqlmpa.dwitchengine.actions.playcard

import ch.qscqlmpa.dwitchengine.actions.GameUpdaterBase
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameEvent
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.game.PlayedCards
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId

internal class PlayCardGameUpdater(
    currentGameState: DwitchGameState
) : GameUpdaterBase(currentGameState) {

    fun clearTable(cardPlayed: PlayedCards) {
        gameStateMutable.moveCardsFromTableToGraveyard()
        gameStateMutable.dwitchGameEvent = DwitchGameEvent.TableHasBeenCleared(cardPlayed)
    }

    fun takeCardsFromHandAndPutOnTable(playerId: DwitchPlayerId, cardsPlayed: PlayedCards) {
        gameStateMutable.removeCardsFromHand(playerId, cardsPlayed)
        gameStateMutable.addCardsToTable(cardsPlayed)
    }

    fun dwitchPlayer(playerId: DwitchPlayerId) {
        gameStateMutable.dwitchPlayer(playerId)
    }
}
