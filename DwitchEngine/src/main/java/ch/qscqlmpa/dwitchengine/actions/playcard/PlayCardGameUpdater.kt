package ch.qscqlmpa.dwitchengine.actions.playcard

import ch.qscqlmpa.dwitchengine.actions.GameUpdaterBase
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.GameEvent
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId

internal class PlayCardGameUpdater(
    currentGameState: GameState,
    private val cardPlayed: Card
) : GameUpdaterBase(currentGameState) {

    fun clearTable() {
        gameStateMutable.moveCardsFromTableToGraveyard()
        gameStateMutable.gameEvent = GameEvent.TableHasBeenCleared(cardPlayed)
    }

    fun takeCardFromHandAndPutOnTable(playerId: PlayerInGameId, cardPlayed: Card) {
        gameStateMutable.removeCardFromHand(playerId, cardPlayed)
        gameStateMutable.addCardToTable(cardPlayed)
    }

    fun dwitchPlayer(playerId: PlayerInGameId) {
        gameStateMutable.dwitchPlayer(playerId)
    }
}