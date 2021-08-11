package ch.qscqlmpa.dwitchengine

import ch.qscqlmpa.dwitchengine.computerplayer.DwitchComputerPlayerEngine
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId

interface DwitchFactory {

    fun createDwitchEngine(gameState: DwitchGameState): DwitchEngine

    fun createComputerPlayerEngine(
        gameState: DwitchGameState,
        computerPlayersId: Set<DwitchPlayerId>
    ): DwitchComputerPlayerEngine
}
