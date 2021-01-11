package ch.qscqlmpa.dwitchengine.actions

import ch.qscqlmpa.dwitchengine.model.game.GamePhase
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.game.GameStateMutable
import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId
import ch.qscqlmpa.dwitchengine.model.player.PlayerStatus
import ch.qscqlmpa.dwitchengine.model.player.Rank
import ch.qscqlmpa.dwitchengine.model.player.SpecialRuleBreaker

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

    fun roundIsOver(newRanks: Map<PlayerDwitchId, Rank>) {
        gameStateMutable.phase = GamePhase.RoundIsOver
        newRanks.forEach { (id, rank) -> gameStateMutable.setPlayerRank(id, rank) }
    }

    fun setPlayerState(playerId: PlayerDwitchId, state: PlayerStatus) {
        gameStateMutable.setPlayerState(playerId, state)
    }

    fun playerIsDone(playerId: PlayerDwitchId, playerDoneWithJoker: Boolean) {
        gameStateMutable.removePlayerFromActivePlayers(playerId)
        gameStateMutable.addDonePlayer(playerId, playerDoneWithJoker)
        gameStateMutable.setPlayerState(playerId, PlayerStatus.Done)
    }

    fun setPlayersWhoPassedTheirTurnedToWaiting() {
        gameStateMutable.allPlayers()
                .filter { player -> player.state == PlayerStatus.TurnPassed }
                .forEach { player -> player.state = PlayerStatus.Waiting }
    }

    fun updateCurrentPlayer(playerId: PlayerDwitchId) {
        gameStateMutable.currentPlayerId = playerId
        gameStateMutable.setPlayerState(playerId, PlayerStatus.Playing)
    }

    fun buildUpdatedGameState(): GameState {
        return gameStateMutable.toGameState()
    }

    fun resetPlayerHasPickedCard(playerId: PlayerDwitchId) {
        gameStateMutable.players.getValue(playerId).hasPickedCard = false
    }

    fun playerPlayedOnTheFirstJokerPlayedOfTheRound(playerId: PlayerDwitchId) {
        gameStateMutable.playersWhoBrokeASpecialRule.add(SpecialRuleBreaker.PlayedOnFirstJack(playerId))
    }
}