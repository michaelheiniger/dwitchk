package ch.qscqlmpa.dwitchengine

import ch.qscqlmpa.dwitchengine.model.game.GameState

class ProdDwitchEngineFactory : DwitchEngineFactory {

    override fun create(gameState: GameState): DwitchEngine {
        return DwitchEngineImpl(gameState)
    }
}