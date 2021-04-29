package ch.qscqlmpa.dwitchengine

import ch.qscqlmpa.dwitchengine.computerplayer.DwitchComputerPlayerEngine
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId

class TestDwitchFactory : DwitchFactory {

    private var dwitchEngine: DwitchEngine? = null
    private var computerPlayerEngine: DwitchComputerPlayerEngine? = null

    fun setInstance(instance: DwitchEngine) {
        dwitchEngine = instance
    }

    fun setInstance(instance: DwitchComputerPlayerEngine) {
        computerPlayerEngine = instance
    }

    override fun createDwitchEngine(gameState: DwitchGameState): DwitchEngine {
        return dwitchEngine ?: throw IllegalStateException("A DwitchEngine instance must be provided")
    }

    override fun createComputerPlayerEngine(
        gameState: DwitchGameState,
        computerPlayersId: Set<DwitchPlayerId>
    ): DwitchComputerPlayerEngine {
        return computerPlayerEngine ?: throw IllegalStateException("A ComputerPlayerEngine instance must be provided")
    }
}
