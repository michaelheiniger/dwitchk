package ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom

import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicationState
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicationStateRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.usecases.CancelGameUsecase
import ch.qscqlmpa.dwitchgame.ongoinggame.usecases.GameLaunchableEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.usecases.GameLaunchableUsecase
import ch.qscqlmpa.dwitchgame.ongoinggame.usecases.LaunchGameUsecase
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

internal class WaitingRoomHostFacadeImpl @Inject constructor(
    private val communicationStateRepository: HostCommunicationStateRepository,
    private val gameLaunchableUsecase: GameLaunchableUsecase,
    private val launchGameUsecase: LaunchGameUsecase,
    private val cancelGameUsecase: CancelGameUsecase,
    private val schedulerFactory: SchedulerFactory
) : WaitingRoomHostFacade {

    override fun observeCommunicationState(): Observable<HostCommunicationState> {
        return communicationStateRepository.currentState()
            .subscribeOn(schedulerFactory.io())
    }

    override fun observeGameLaunchableEvents(): Observable<GameLaunchableEvent> {
        return gameLaunchableUsecase.gameCanBeLaunched()
            .subscribeOn(schedulerFactory.io())
    }

    override fun launchGame(): Completable {
        return launchGameUsecase.launchGame()
            .subscribeOn(schedulerFactory.io())
    }

    override fun cancelGame(): Completable {
        return cancelGameUsecase.cancelGame()
            .subscribeOn(schedulerFactory.io())
    }
}