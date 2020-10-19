package ch.qscqlmpa.dwitch.ongoinggame.usecases

import ch.qscqlmpa.dwitch.ongoinggame.InGameStore
import ch.qscqlmpa.dwitch.ongoinggame.ServiceManager
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.messages.HostMessageFactory
import io.reactivex.Completable
import io.reactivex.Single
import javax.inject.Inject

class CancelGameUsecase @Inject
constructor(private val store: InGameStore,
            private val communicator: HostCommunicator,
            private val serviceManager: ServiceManager
) {

    fun cancelGame(): Completable {
        return Single.fromCallable {
            store.deleteGame()
            return@fromCallable HostMessageFactory.createCancelGameMessage()
        }.flatMapCompletable(communicator::sendMessage)
                .andThen(releaseResources())
    }

    private fun releaseResources(): Completable {
        return Completable.fromAction {
            communicator.closeAllConnections()
            serviceManager.stopHostService()
        }
    }
}
