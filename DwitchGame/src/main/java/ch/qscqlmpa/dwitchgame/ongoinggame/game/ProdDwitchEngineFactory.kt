package ch.qscqlmpa.dwitchgame.ongoinggame.game

import ch.qscqlmpa.dwitchengine.DwitchEngine
import ch.qscqlmpa.dwitchengine.model.game.GameState

class ProdDwitchEngineFactory : DwitchEngineFactory{

    override fun create(gameState: GameState): DwitchEngine {
        return DwitchEngine(gameState)
    }
}