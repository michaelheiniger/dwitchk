package ch.qscqlmpa.dwitchengine.actions

import ch.qscqlmpa.dwitchengine.model.game.GamePhase
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import ch.qscqlmpa.dwitchengine.model.player.PlayerState

internal abstract class GameStateBase(private val currentGameState: GameState) {

    open fun checkState() {
        checkLocalPlayerIsCurrentPlayer(currentGameState)
        checkLocalPlayerState(currentGameState)
    }

    fun numPlayersTotal(): Int {
        return currentGameState.players.size
    }

    fun gamePhaseIsRoundIsBeginning(): Boolean {
        return currentGameState.phase == GamePhase.RoundIsBeginning
    }

    fun localPlayerId(): PlayerInGameId {
        return currentGameState.localPlayerId
    }

    private fun checkLocalPlayerIsCurrentPlayer(gameState: GameState) {
        if (gameState.localPlayer() != gameState.currentPlayer()) {
            throw java.lang.IllegalStateException("Local player (id: ${gameState.localPlayer().inGameId}) cannot play because it is NOT the current player (id: ${gameState.currentPlayer().inGameId}).")
        }
    }

    private fun checkLocalPlayerState(currentGameState: GameState) {
        val playerState = currentGameState.localPlayer().state
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