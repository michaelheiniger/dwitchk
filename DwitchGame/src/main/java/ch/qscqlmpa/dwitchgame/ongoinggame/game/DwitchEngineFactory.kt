package ch.qscqlmpa.dwitchgame.ongoinggame.game

import ch.qscqlmpa.dwitchengine.DwitchEngine
import ch.qscqlmpa.dwitchengine.model.game.GameState

internal interface DwitchEngineFactory {

    fun create(gameState: GameState): DwitchEngine
}