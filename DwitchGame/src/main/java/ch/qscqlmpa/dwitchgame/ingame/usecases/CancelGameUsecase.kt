package ch.qscqlmpa.dwitchgame.ingame.usecases

import ch.qscqlmpa.dwitchgame.gamelifecycleevents.HostGameLifecycleEvent
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.HostGameLifecycleEventRepository
import ch.qscqlmpa.dwitchgame.ingame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchgame.ingame.communication.messagefactories.HostMessageFactory
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

internal class CancelGameUsecase @Inject constructor(
    private val store: InGameStore,
    private val communicator: HostCommunicator,
    private val gameLifecycleEventRepository: HostGameLifecycleEventRepository
) {

    fun cancelGame(): Completable {
        return Completable.fromAction {
            if (store.gameIsNew()) {
                store.markGameForDeletion()
            }
            communicator.sendMessage(HostMessageFactory.createCancelGameMessage()) // Notify guests
            gameLifecycleEventRepository.notify(HostGameLifecycleEvent.GameOver)
        }
    }
}
