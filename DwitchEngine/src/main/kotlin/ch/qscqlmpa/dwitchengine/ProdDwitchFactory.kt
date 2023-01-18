package ch.qscqlmpa.dwitchengine

import ch.qscqlmpa.dwitchengine.computerplayer.ComputerPlayerEngineImpl
import ch.qscqlmpa.dwitchengine.computerplayer.ComputerReflexionTime
import ch.qscqlmpa.dwitchengine.computerplayer.DwitchComputerPlayerEngine
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId

class ProdDwitchFactory(private val computerReflexionTime: ComputerReflexionTime) : DwitchFactory {

    override fun createDwitchEngine(gameState: DwitchGameState): DwitchEngine = DwitchEngineImpl(gameState)

    override fun createComputerPlayerEngine(
        gameState: DwitchGameState,
        computerPlayersId: Set<DwitchPlayerId>
    ): DwitchComputerPlayerEngine {
        return ComputerPlayerEngineImpl(createDwitchEngine(gameState), computerPlayersId, computerReflexionTime)
    }
}
