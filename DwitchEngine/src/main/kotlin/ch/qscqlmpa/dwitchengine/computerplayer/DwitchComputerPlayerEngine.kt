package ch.qscqlmpa.dwitchengine.computerplayer

import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId

interface DwitchComputerPlayerEngine {

    fun handleComputerPlayerAction(): List<ComputerPlayerActionResult>
}

data class ComputerPlayerActionResult(
    val dwitchId: DwitchPlayerId,
    val updatedGameState: DwitchGameState
)
