package ch.qscqlmpa.dwitchengine

import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState

interface DwitchEngineFactory {

    fun create(gameState: DwitchGameState): DwitchEngine
}
