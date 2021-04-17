package ch.qscqlmpa.dwitchengine.actions

import ch.qscqlmpa.dwitchengine.model.game.DwitchGamePhase
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerStatus

internal abstract class GameStateBase(private val currentGameState: DwitchGameState) {

    open fun checkState() {
    }

    fun numPlayersTotal(): Int {
        return currentGameState.players.size
    }

    fun currentPlayerId(): DwitchPlayerId {
        return currentGameState.currentPlayerId
    }

    protected fun checkCurrentPlayerStateIsPlaying() {
        val playerState = currentGameState.currentPlayer().status
        if (DwitchPlayerStatus.Playing != playerState) {
            throw IllegalStateException("Player must be in state ${DwitchPlayerStatus.Playing} (actual state: $playerState)")
        }
    }

    protected fun checkGamePhase(expectedPhase: DwitchGamePhase) {
        if (currentGameState.phase != expectedPhase) {
            throw IllegalStateException("Expected game phase is $expectedPhase but actual value is ${currentGameState.phase}")
        }
    }
}
