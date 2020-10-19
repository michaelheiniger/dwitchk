package ch.qscqlmpa.dwitchengine.actions

import ch.qscqlmpa.dwitchengine.model.game.GameInfo
import ch.qscqlmpa.dwitchengine.model.game.GamePhase
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.game.GameStateMutable
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import ch.qscqlmpa.dwitchengine.model.player.PlayerState
import ch.qscqlmpa.dwitchengine.model.player.Rank

internal abstract class GameUpdaterBase(currentGameState: GameState) {

    protected val gameStateMutable = GameStateMutable.fromGameState(currentGameState)

    fun undwitchAllPlayers() {
        gameStateMutable.undwitchAllPlayers()
    }

    fun resetGameEvent() {
        gameStateMutable.gameEvent = null
    }

    fun setGamePhase(phase: GamePhase) {
        gameStateMutable.phase = phase
    }

    fun roundIsOver(newRanks: Map<PlayerInGameId, Rank>) {
        gameStateMutable.phase = GamePhase.RoundIsOver
        newRanks.forEach { (id, rank) -> gameStateMutable.setPlayerRank(id, rank) }
    }

    fun setPlayerState(playerId: PlayerInGameId, state: PlayerState) {
        gameStateMutable.setPlayerState(playerId, state)
    }

    fun playerIsDone(playerId: PlayerInGameId, playerDoneWithJoker: Boolean) {
        gameStateMutable.removePlayerFromActivePlayers(playerId)
        gameStateMutable.addDonePlayer(playerId, playerDoneWithJoker)
        gameStateMutable.setPlayerState(playerId, PlayerState.Done)
    }

    fun setPlayersWhoPassedTheirTurnedToWaiting() {
        gameStateMutable.allPlayers()
                .filter { player -> player.state == PlayerState.TurnPassed }
                .forEach { player -> player.state = PlayerState.Waiting }
    }

    fun updateCurrentPlayer(playerId: PlayerInGameId) {
        gameStateMutable.currentPlayerId = playerId
        gameStateMutable.setPlayerState(playerId, PlayerState.Playing)
    }

    fun buildUpdatedGameState(): GameInfo {
        return GameInfo(gameStateMutable.toGameState(), gameStateMutable.localPlayerId)
    }

    fun resetPlayerHasPickedCard(playerId: PlayerInGameId) {
        gameStateMutable.players.getValue(playerId).hasPickedCard = false
    }
}