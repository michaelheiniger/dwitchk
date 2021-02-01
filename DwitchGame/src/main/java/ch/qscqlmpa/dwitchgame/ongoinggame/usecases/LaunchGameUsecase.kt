package ch.qscqlmpa.dwitchgame.ongoinggame.usecases

import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.HostMessageFactory
import ch.qscqlmpa.dwitchgame.ongoinggame.services.ChangeCurrentRoomService
import ch.qscqlmpa.dwitchgame.ongoinggame.services.GameInitializerService
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single
import timber.log.Timber
import javax.inject.Inject


internal class LaunchGameUsecase @Inject constructor(
    private val store: InGameStore,
    private val gameInitializerService: GameInitializerService,
    private val changeCurrentRoomService: ChangeCurrentRoomService,
    private val communicator: HostCommunicator
) {

    fun launchGame(): Completable {
        return Single.fromCallable { store.gameIsNew() }
            .flatMap { gameIsNew ->
                if (gameIsNew) {
                    gameInitializerService.initializeGameState()
                } else {
                    fetchExistingGameState()
                }
            }.flatMapCompletable(::sendLaunchGameMessage)
            .andThen(changeCurrentRoomService.moveToGameRoom())
            .doOnError { e -> Timber.e(e, "Error while launching game") }
    }

    private fun fetchExistingGameState(): Single<GameState> {
        return Single.fromCallable { store.getGameState() }
    }

    private fun sendLaunchGameMessage(gameState: GameState): Completable {
        return Single.fromCallable { HostMessageFactory.createLaunchGameMessage(gameState) }
            .flatMapCompletable(communicator::sendMessage)
    }
}