package ch.qscqlmpa.dwitchgame.ingame.services

import ch.qscqlmpa.dwitchengine.DwitchEngine
import ch.qscqlmpa.dwitchengine.initialgamesetup.InitialGameSetupFactory
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerOnboardingInfo
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import javax.inject.Inject

// TODO: Merge with LaunchGameUsecase
internal class GameInitializerService @Inject constructor(
    private val store: InGameStore,
    private val initialGameSetupFactory: InitialGameSetupFactory
) {

    fun initializeGameState(): DwitchGameState {
        val players = store.getPlayersInWaitingRoom().map { p -> DwitchPlayerOnboardingInfo(p.dwitchId, p.name) }
        val initialGameSetup = initialGameSetupFactory.getInitialGameSetup(players.map { p -> p.id }.toSet())
        val gameState = DwitchEngine.createNewGame(players, initialGameSetup)
        store.updateGameState(gameState)
        return gameState
    }
}
