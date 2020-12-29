package ch.qscqlmpa.dwitchengine.actions.startnewround

import ch.qscqlmpa.dwitchengine.actions.GameStateBase
import ch.qscqlmpa.dwitchengine.model.game.GamePhase
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.Player
import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId
import ch.qscqlmpa.dwitchengine.model.player.Rank

internal class StartNewRoundState(private val currentGameState: GameState) : GameStateBase(currentGameState) {

    override fun checkState() {
        super.checkState()
        checkCurrentPlayerStateIsPlaying()
        checkGamePhase(GamePhase.RoundIsOver)
    }

    fun getAllPlayers(): List<Player> {
        return currentGameState.players.map { (_, player) -> player }
    }

    fun getAllPlayersId(): List<PlayerDwitchId> {
        return currentGameState.players.map { (_, player) -> player.id }
    }

    fun asshole(): PlayerDwitchId {
        return currentGameState.players
                .map { (_, player) -> player }
                .find { player -> player.rank == Rank.Asshole }!!.id
    }
}