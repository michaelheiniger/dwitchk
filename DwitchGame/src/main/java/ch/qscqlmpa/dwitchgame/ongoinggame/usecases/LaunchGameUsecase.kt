package ch.qscqlmpa.dwitchgame.ongoinggame.usecases

import ch.qscqlmpa.dwitchengine.DwitchEngine
import ch.qscqlmpa.dwitchengine.initialgamesetup.InitialGameSetupFactory
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchgame.appevent.AppEvent
import ch.qscqlmpa.dwitchgame.appevent.AppEventRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.HostMessageFactory
import ch.qscqlmpa.dwitchmodel.game.RoomType
import ch.qscqlmpa.dwitchmodel.player.Player
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.Completable
import io.reactivex.Single
import timber.log.Timber
import javax.inject.Inject


internal class LaunchGameUsecase @Inject constructor(
    private val store: InGameStore,
    private val communicator: HostCommunicator,
    private val appEventRepository: AppEventRepository,
    private val initialGameSetupFactory: InitialGameSetupFactory,
) {

    fun launchGame(): Completable {
        return initializeGameState()
            .flatMapCompletable { gameState ->
                Completable.merge(
                    listOf(
                        saveGameStateInStore(gameState),
                        setCurrentRoomToGameRoomInStore(),
                        sendLaunchGameMessage(gameState)
                    )
                )
            }
            .doOnComplete { setCurrentRoomToGameRoomInService() }
            .doOnError { e -> Timber.e(e, "Error while launching game") }
    }

    private fun initializeGameState(): Single<GameState> {
        return Single.fromCallable {
            val players = store.getPlayersInWaitingRoom().map(Player::toPlayerInfo)
            val initialGameSetup = initialGameSetupFactory.getInitialGameSetup(players.size)
            return@fromCallable DwitchEngine.createNewGame(players, initialGameSetup)
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
        appEventRepository.notify(AppEvent.GameRoomJoinedByHost)
    }
}