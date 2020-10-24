package ch.qscqlmpa.dwitch.ongoinggame.usecases

import ch.qscqlmpa.dwitch.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GameEvent
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GameEventRepository
import ch.qscqlmpa.dwitch.ongoinggame.messages.HostMessageFactory
import ch.qscqlmpa.dwitch.ongoinggame.persistence.InGameStore
import ch.qscqlmpa.dwitch.ongoinggame.services.ServiceManager
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class CancelGameUsecase @Inject
constructor(private val store: InGameStore,
            private val communicator: HostCommunicator,
            private val serviceManager: ServiceManager,
            private val gameEventRepository: GameEventRepository
) {

    fun cancelGame(): Completable {
        return Single.fromCallable {
            store.deleteGame()
            return@fromCallable HostMessageFactory.createCancelGameMessage()
        }.flatMapCompletable(communicator::sendMessage)
                .andThen(releaseResources())
            .doOnComplete { gameEventRepository.notifyOfEvent(GameEvent.GameCanceled) }
    }

    private fun releaseResources(): Completable {
        return Completable.fromAction {
            communicator.closeAllConnections()
            serviceManager.stopHostService()
        }
    }
}
