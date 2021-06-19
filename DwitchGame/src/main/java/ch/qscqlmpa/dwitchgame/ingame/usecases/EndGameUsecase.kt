package ch.qscqlmpa.dwitchgame.ingame.usecases

import ch.qscqlmpa.dwitchgame.gamelifecycleevents.HostGameLifecycleEvent
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.HostGameLifecycleEventRepository
import ch.qscqlmpa.dwitchgame.ingame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchgame.ingame.communication.messagefactories.HostMessageFactory
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

internal class EndGameUsecase @Inject constructor(
    private val gameLifecycleEventRepository: HostGameLifecycleEventRepository,
    private val communicator: HostCommunicator
) {

    fun endGame(): Completable {
        return Completable.fromAction {
            communicator.sendMessage(HostMessageFactory.createGameOverMessage())
            gameLifecycleEventRepository.notify(HostGameLifecycleEvent.GameOver)
        }
    }
}
