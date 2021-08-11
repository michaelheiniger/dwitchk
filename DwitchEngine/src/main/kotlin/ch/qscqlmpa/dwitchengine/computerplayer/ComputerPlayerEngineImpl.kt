package ch.qscqlmpa.dwitchengine.computerplayer

import ch.qscqlmpa.dwitchengine.DwitchEngine
import ch.qscqlmpa.dwitchengine.model.game.DwitchGamePhase
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId

internal class ComputerPlayerEngineImpl(
    private val dwitchEngine: DwitchEngine,
    private val computerPlayersId: Set<DwitchPlayerId>
) : DwitchComputerPlayerEngine {

    private val gameInfo = dwitchEngine.getGameInfo()

    override fun handleComputerPlayerAction(): List<ComputerPlayerActionResult> {
        return when (gameInfo.gamePhase) {
            DwitchGamePhase.RoundIsBeginning,
            DwitchGamePhase.RoundIsOnGoing -> playIfNeeded()
            DwitchGamePhase.CardExchange -> performCardExchange()
            else -> emptyList() // Nothing to do
        }
    }

    private fun playIfNeeded(): List<ComputerPlayerActionResult> {
        val playingPlayerId = computerPlayersId.find { id -> id == gameInfo.currentPlayerId }
            ?: return emptyList() // It is not the turn of a computer player
        return listOf(ComputerPlayEngine(dwitchEngine, playingPlayerId).play())
    }

    private fun performCardExchange(): List<ComputerPlayerActionResult> {
        return ComputerCardExchangeEngine(dwitchEngine, computerPlayersId).performCardExchangeIfNeeded()
    }
}
