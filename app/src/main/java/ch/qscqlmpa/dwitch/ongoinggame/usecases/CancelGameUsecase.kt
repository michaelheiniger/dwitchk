package ch.qscqlmpa.dwitch.ongoinggame.usecases

import ch.qscqlmpa.dwitch.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GuestGameEvent
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GuestGameEventRepository
import ch.qscqlmpa.dwitch.ongoinggame.messages.HostMessageFactory
import ch.qscqlmpa.dwitch.ongoinggame.persistence.InGameStore
import ch.qscqlmpa.dwitch.ongoinggame.services.ServiceManager
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

internal class CancelGameUsecase @Inject
constructor(
    private val store: InGameStore,
    private val communicator: HostCommunicator,
    private val serviceManager: ServiceManager,
    private val gameEventRepository: GuestGameEventRepository
) {

    fun cancelGame(): Completable {
        return deleteGameFromStore()
            .andThen(sendCancelGameMessage())
            .andThen(releaseResources())
            .doOnComplete { gameEventRepository.notify(GuestGameEvent.GameCanceled) }
    }

    private fun deleteGameFromStore(): Completable {
        return Completable.fromAction { store.deleteGame() }
    }

    private fun sendCancelGameMessage(): Completable {
        return Single.fromCallable { HostMessageFactory.createCancelGameMessage() }
            .flatMapCompletable(communicator::sendMessage)
    }

    private fun releaseResources(): Completable {
        return Completable.fromAction { serviceManager.stopHostService() }
    }
}
