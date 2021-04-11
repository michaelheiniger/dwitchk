package ch.qscqlmpa.dwitchengine.actions.startnewround

import ch.qscqlmpa.dwitchengine.actions.GameStateBase
import ch.qscqlmpa.dwitchengine.model.game.DwitchGamePhase
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayer
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank

internal class StartNewRoundState(private val currentGameState: DwitchGameState) : GameStateBase(currentGameState) {

    override fun checkState() {
        super.checkState()
        checkCurrentPlayerStateIsPlaying()
        checkGamePhase(DwitchGamePhase.RoundIsOver)
    }

    fun getAllPlayers(): List<DwitchPlayer> {
        return currentGameState.players.map { (_, player) -> player }
    }

    fun getAllPlayersId(): List<DwitchPlayerId> {
        return currentGameState.players.map { (_, player) -> player.id }
    }

    fun asshole(): DwitchPlayerId {
        return currentGameState.players
            .map { (_, player) -> player }
            .find { player -> player.rank == DwitchRank.Asshole }!!.id
    }
}
