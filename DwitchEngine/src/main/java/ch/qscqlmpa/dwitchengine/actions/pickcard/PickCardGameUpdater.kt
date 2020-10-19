package ch.qscqlmpa.dwitchengine.actions.pickcard

import ch.qscqlmpa.dwitchengine.actions.GameUpdaterBase
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId

internal class PickCardGameUpdater(currentGameState: GameState) : GameUpdaterBase(currentGameState) {

    private val localPlayerId = currentGameState.localPlayerId

    fun pickCardFromDeckAndPutItInHands() {
        val cardPicked = gameStateMutable.removeTopCardFromDeck()
        gameStateMutable.addCardToHand(localPlayerId, cardPicked)
    }

    fun setPlayerHasPickedCard(playerId: PlayerInGameId) {
        gameStateMutable.players.getValue(playerId).hasPickedCard = true
    }
}