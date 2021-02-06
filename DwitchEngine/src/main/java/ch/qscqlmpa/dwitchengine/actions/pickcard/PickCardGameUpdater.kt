package ch.qscqlmpa.dwitchengine.actions.pickcard

import ch.qscqlmpa.dwitchengine.actions.GameUpdaterBase
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId

internal class PickCardGameUpdater(private val currentGameState: GameState) : GameUpdaterBase(currentGameState) {

    fun pickCardFromDeckAndPutItInHands() {
        val cardPicked = gameStateMutable.removeTopCardFromDeck()
        gameStateMutable.addCardToHand(currentGameState.currentPlayerId, cardPicked)
    }

    fun setPlayerHasPickedCard(playerId: PlayerDwitchId) {
        gameStateMutable.players.getValue(playerId).hasPickedCard = true
    }
}
