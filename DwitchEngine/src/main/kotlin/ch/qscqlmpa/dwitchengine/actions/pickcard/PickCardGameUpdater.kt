package ch.qscqlmpa.dwitchengine.actions.pickcard

import ch.qscqlmpa.dwitchengine.actions.GameUpdaterBase
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId

internal class PickCardGameUpdater(private val currentGameState: DwitchGameState) : GameUpdaterBase(currentGameState) {

    fun pickCardFromDeckAndPutItInHands() {
        val cardPicked = gameStateMutable.removeTopCardFromDeck()
        gameStateMutable.addCardToHand(currentGameState.currentPlayerId, cardPicked)
    }

    fun setPlayerHasPickedCard(playerId: DwitchPlayerId) {
        gameStateMutable.players.getValue(playerId).hasPickedCard = true
    }
}
