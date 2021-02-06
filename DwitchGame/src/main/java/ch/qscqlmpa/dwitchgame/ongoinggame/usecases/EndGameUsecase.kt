package ch.qscqlmpa.dwitchgame.ongoinggame.usecases

import ch.qscqlmpa.dwitchgame.appevent.AppEvent
import ch.qscqlmpa.dwitchgame.appevent.AppEventRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.HostMessageFactory
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

internal class EndGameUsecase @Inject constructor(
    private val appEventRepository: AppEventRepository,
    private val communicator: HostCommunicator
) {

    fun endGame(): Completable {
        return Completable.fromAction {
            communicator.sendMessage(HostMessageFactory.createGameOverMessage())
            appEventRepository.notify(AppEvent.GameOver)
        }
    }
}
