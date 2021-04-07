package ch.qscqlmpa.dwitchengine

import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState

class ProdDwitchEngineFactory : DwitchEngineFactory {

    override fun create(gameState: DwitchGameState): DwitchEngine {
        return DwitchEngineImpl(gameState)
    }
}
