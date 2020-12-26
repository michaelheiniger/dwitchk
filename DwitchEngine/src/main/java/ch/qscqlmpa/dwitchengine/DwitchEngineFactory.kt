package ch.qscqlmpa.dwitchengine

import ch.qscqlmpa.dwitchengine.model.game.GameState

interface DwitchEngineFactory {

    fun create(gameState: GameState): DwitchEngine
}