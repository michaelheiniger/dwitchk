package ch.qscqlmpa.dwitchgame.ongoinggame.services

import ch.qscqlmpa.dwitchengine.DwitchEngine
import ch.qscqlmpa.dwitchengine.initialgamesetup.InitialGameSetupFactory
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchmodel.player.Player
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.rxjava3.core.Single
import javax.inject.Inject

internal class GameInitializerService @Inject constructor(
    private val store: InGameStore,
    private val initialGameSetupFactory: InitialGameSetupFactory
){

    fun initializeGameState(): Single<GameState> {
        return Single.fromCallable {
            val players = store.getPlayersInWaitingRoom().map(Player::toPlayerInfo)
            val initialGameSetup = initialGameSetupFactory.getInitialGameSetup(players.size)
            val gameState = DwitchEngine.createNewGame(players, initialGameSetup)
            store.updateGameState(gameState)
            gameState
        }
    }
}