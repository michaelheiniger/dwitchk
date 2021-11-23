package ch.qscqlmpa.dwitchengine.actions

import ch.qscqlmpa.dwitchengine.model.game.DwitchGamePhase
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.game.GameStateMutable
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerStatus
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank
import ch.qscqlmpa.dwitchengine.model.player.SpecialRuleBreaker

internal abstract class GameUpdaterBase(currentGameState: DwitchGameState) {

    protected val gameStateMutable = GameStateMutable.fromGameState(currentGameState)
    protected val currentPlayerId = currentGameState.currentPlayerId

    fun undwitchAllPlayers(): GameUpdaterBase {
        gameStateMutable.undwitchAllPlayers()
        return this
    }

    fun resetGameEvent() {
        gameStateMutable.lastPlayerAction = null
    }

    fun setGamePhase(phase: DwitchGamePhase) {
        gameStateMutable.phase = phase
    }

    fun roundIsOver(newRanks: Map<DwitchPlayerId, DwitchRank>) {
        gameStateMutable.phase = DwitchGamePhase.RoundIsOver
        newRanks.forEach { (id, rank) -> gameStateMutable.setPlayerRank(id, rank) }
    }

    fun setPlayerState(playerId: DwitchPlayerId, state: DwitchPlayerStatus) {
        gameStateMutable.setPlayerState(playerId, state)
    }

    fun playerIsDone(playerId: DwitchPlayerId, playerDoneWithJoker: Boolean) {
        gameStateMutable.removePlayerFromActivePlayers(playerId)
        gameStateMutable.addDonePlayer(playerId, playerDoneWithJoker)
        gameStateMutable.setPlayerState(playerId, DwitchPlayerStatus.Done)
    }

    fun setPlayersWhoPassedTheirTurnedToWaiting(): GameStateMutable {
        gameStateMutable.allPlayers()
            .filter { player -> player.status == DwitchPlayerStatus.TurnPassed }
            .forEach { player -> player.status = DwitchPlayerStatus.Waiting }
        return gameStateMutable
    }

    fun updateCurrentPlayer(playerId: DwitchPlayerId): GameUpdaterBase {
        gameStateMutable.currentPlayerId = playerId
        gameStateMutable.setPlayerState(playerId, DwitchPlayerStatus.Playing)
        return this
    }

    open fun buildUpdatedGameState(): DwitchGameState {
        return gameStateMutable.toGameState()
    }

    fun playerPlayedOnTheFirstJokerPlayedOfTheRound(playerId: DwitchPlayerId) {
        gameStateMutable.playersWhoBrokeASpecialRule.add(SpecialRuleBreaker.PlayedOnFirstJack(playerId))
    }
}
