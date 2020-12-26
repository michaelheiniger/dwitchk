package ch.qscqlmpa.dwitchengine

import ch.qscqlmpa.dwitchengine.model.game.GameState

class TestDwitchEngineFactory : DwitchEngineFactory {

    private var dwitchEngine: DwitchEngine? = null

    fun setInstance(instance: DwitchEngine) {
        dwitchEngine = instance
    }

    override fun create(gameState: GameState): DwitchEngine {
        return dwitchEngine
            ?: throw IllegalStateException("An DwitchEngine instance must be provided")
    }
}