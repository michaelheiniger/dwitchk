package ch.qscqlmpa.dwitchengine.actions

import ch.qscqlmpa.dwitchengine.model.game.GamePhase
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId
import ch.qscqlmpa.dwitchengine.model.player.PlayerStatus

internal abstract class GameStateBase(private val currentGameState: GameState) {

    open fun checkState() {

    }

    fun numPlayersTotal(): Int {
        return currentGameState.players.size
    }

    fun currentPlayerId(): PlayerDwitchId {
        return currentGameState.currentPlayerId
    }

    protected fun checkCurrentPlayerStateIsPlaying() {
        val playerState = currentGameState.currentPlayer().status
        if (PlayerStatus.Playing != playerState) {
            throw IllegalStateException("Player is not in state $playerState")
        }
    }

    protected fun checkGamePhase(expectedPhase: GamePhase) {
        if (currentGameState.phase != expectedPhase) {
            throw IllegalStateException("Expected game phase is $expectedPhase but actual value is ${currentGameState.phase}")
        }
    }
}