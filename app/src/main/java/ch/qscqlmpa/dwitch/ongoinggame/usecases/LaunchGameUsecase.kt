package ch.qscqlmpa.dwitch.ongoinggame.usecases

import ch.qscqlmpa.dwitch.model.RoomType
import ch.qscqlmpa.dwitch.model.player.Player
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.messages.HostMessageFactory
import ch.qscqlmpa.dwitch.ongoinggame.persistence.InGameStore
import ch.qscqlmpa.dwitch.ongoinggame.services.ServiceManager
import ch.qscqlmpa.dwitchengine.DwitchEngine
import ch.qscqlmpa.dwitchengine.initialgamesetup.InitialGameSetupFactory
import ch.qscqlmpa.dwitchengine.model.game.GameState
import io.reactivex.Completable
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject



class LaunchGameUsecase @Inject constructor(private val store: InGameStore,
                                            private val communicator: HostCommunicator,
                                            private val serviceManager: ServiceManager,
                                            private val initialGameSetupFactory: InitialGameSetupFactory
) {

    fun launchGame(): Completable {
        return initializeGameState()
                .flatMapCompletable { gameState ->
                    Completable.merge(listOf(
                            saveGameStateInStore(gameState),
                            setCurrentRoomToGameRoomInStore(),
                            sendLaunchGameMessage(gameState)
                    ))
                }
                .doOnComplete { setCurrentRoomToGameRoomInService() }
            .doOnError { e -> Timber.e(e, "Error while launching game") }
    }

    private fun initializeGameState(): Single<GameState> {
        return Single.fromCallable {
            val players = store.getPlayersInWaitingRoom().map(Player::toPlayerInfo)
            val localPlayerId = store.getLocalPlayerInGameId()
            val initialGameSetup = initialGameSetupFactory.getInitialGameSetup(players.size)
            return@fromCallable DwitchEngine.createNewGame(players, localPlayerId, initialGameSetup)
        }
    }

    private fun saveGameStateInStore(gameState: GameState): Completable {
        return Completable.fromAction { store.updateGameState(gameState) }
    }

    private fun setCurrentRoomToGameRoomInStore(): Completable {
        return Completable.fromAction { store.updateGameRoom(RoomType.GAME_ROOM) }
    }

    private fun sendLaunchGameMessage(gameState: GameState): Completable {
        return Single.fromCallable {
            HostMessageFactory.createLaunchGameMessage(gameState)
        }.flatMapCompletable(communicator::sendMessage)
    }

    private fun setCurrentRoomToGameRoomInService() {
        serviceManager.goToHostGameRoom()
    }
}