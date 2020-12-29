package ch.qscqlmpa.dwitchengine.actions.playcard

import ch.qscqlmpa.dwitchengine.actions.GameUpdaterBase
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.GameEvent
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId


internal class PlayCardGameUpdater(
    currentGameState: GameState
) : GameUpdaterBase(currentGameState) {

    fun clearTable(cardPlayed: Card) {
        gameStateMutable.moveCardsFromTableToGraveyard()
        gameStateMutable.gameEvent = GameEvent.TableHasBeenCleared(cardPlayed)
    }

    fun takeCardFromHandAndPutOnTable(playerId: PlayerDwitchId, cardPlayed: Card) {
        gameStateMutable.removeCardFromHand(playerId, cardPlayed)
        gameStateMutable.addCardToTable(cardPlayed)
    }

    fun dwitchPlayer(playerId: PlayerDwitchId) {
        gameStateMutable.dwitchPlayer(playerId)
    }
}