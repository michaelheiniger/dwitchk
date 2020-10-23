package ch.qscqlmpa.dwitchengine.actions

import ch.qscqlmpa.dwitchengine.model.game.GamePhase
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import ch.qscqlmpa.dwitchengine.model.player.PlayerState

internal abstract class GameStateBase(private val currentGameState: GameState) {

    open fun checkState() {
        checkCurrentPlayerState()
    }

    fun numPlayersTotal(): Int {
        return currentGameState.players.size
    }

    fun gamePhaseIsRoundIsBeginning(): Boolean {
        return currentGameState.phase == GamePhase.RoundIsBeginning
    }

    fun currentPlayerId(): PlayerInGameId {
        return currentGameState.currentPlayerId
    }

    private fun checkCurrentPlayerState() {
        val playerState = currentGameState.currentPlayer().state
        if (PlayerState.Playing != playerState) {
            throw IllegalStateException("Player is not in state $playerState")
        }
    }

    protected fun checkGamePhase(expectedPhase: GamePhase) {
        if (currentGameState.phase != expectedPhase) {
            throw IllegalStateException("Expected game phase is $expectedPhase but actual value is ${currentGameState.phase}")
        }
    }
}