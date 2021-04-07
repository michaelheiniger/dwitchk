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

    fun undwitchAllPlayers() {
        gameStateMutable.undwitchAllPlayers()
    }

    fun resetGameEvent() {
        gameStateMutable.dwitchGameEvent = null
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

    fun setPlayersWhoPassedTheirTurnedToWaiting() {
        gameStateMutable.allPlayers()
            .filter { player -> player.state == DwitchPlayerStatus.TurnPassed }
            .forEach { player -> player.state = DwitchPlayerStatus.Waiting }
    }

    fun updateCurrentPlayer(playerId: DwitchPlayerId) {
        gameStateMutable.currentPlayerId = playerId
        gameStateMutable.setPlayerState(playerId, DwitchPlayerStatus.Playing)
    }

    fun buildUpdatedGameState(): DwitchGameState {
        return gameStateMutable.toGameState()
    }

    fun resetPlayerHasPickedCard(playerId: DwitchPlayerId) {
        gameStateMutable.players.getValue(playerId).hasPickedCard = false
    }

    fun playerPlayedOnTheFirstJokerPlayedOfTheRound(playerId: DwitchPlayerId) {
        gameStateMutable.playersWhoBrokeASpecialRule.add(SpecialRuleBreaker.PlayedOnFirstJack(playerId))
    }
}
