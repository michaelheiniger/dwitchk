package ch.qscqlmpa.dwitchengine.actions.passturn

import ch.qscqlmpa.dwitchengine.actions.GameStateBase
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayer

internal class PassTurnState(private val currentGameState: DwitchGameState) : GameStateBase(currentGameState) {

    override fun checkState() {
        super.checkState()
        checkCurrentPlayerStateIsPlaying()
    }

    fun nextWaitingPlayer(): DwitchPlayer {
        // There must be at least one Waiting player, otherwise the current player would not need to pass turn
        return currentGameState.nextWaitingPlayer()!!
    }

    fun onlyOneOtherPlayerCanPlay(): Boolean {
        return currentGameState.waitingPlayerInOrderAfterLocalPlayer().size == 1
    }
}
