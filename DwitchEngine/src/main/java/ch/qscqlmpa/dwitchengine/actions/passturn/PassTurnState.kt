package ch.qscqlmpa.dwitchengine.actions.passturn

import ch.qscqlmpa.dwitchengine.actions.GameStateBase
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.Player

internal class PassTurnState(private val currentGameState: GameState) : GameStateBase(currentGameState) {

    override fun checkState() {
        super.checkState()
        checkPlayerHasPickedACard(currentGameState)
    }

    fun nextWaitingPlayer(): Player {
        // There must be at least one Waiting player, otherwise the current player would not need to pass turn
        return currentGameState.nextWaitingPlayer()!!
    }

    fun onlyOneOtherPlayerCanPlay(): Boolean {
        return currentGameState.waitingPlayerInOrderAfterLocalPlayer().size == 1
    }

    private fun checkPlayerHasPickedACard(currentGameState: GameState) {
        if (!currentGameState.currentPlayer().hasPickedACard) {
            throw IllegalStateException("The player must first pick a card and then is allowed to pass its turn.")
        }
    }
}