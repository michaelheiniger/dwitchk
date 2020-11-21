package ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom

import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicationState
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.usecases.CancelGameUsecase
import ch.qscqlmpa.dwitchgame.ongoinggame.usecases.GameLaunchableEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.usecases.GameLaunchableUsecase
import ch.qscqlmpa.dwitchgame.ongoinggame.usecases.LaunchGameUsecase
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject

internal class WaitingRoomHostFacadeImpl @Inject constructor(
    private val hostCommunicator: HostCommunicator,
    private val gameLaunchableUsecase: GameLaunchableUsecase,
    private val launchGameUsecase: LaunchGameUsecase,
    private val cancelGameUsecase: CancelGameUsecase
) : WaitingRoomHostFacade {

    override fun observeCommunicationState(): Observable<HostCommunicationState> {
        return hostCommunicator.observeCommunicationState()
    }

    override fun gameCanBeLaunched(): Observable<GameLaunchableEvent> {
        return gameLaunchableUsecase.gameCanBeLaunched()
    }

    override fun launchGame(): Completable {
        return launchGameUsecase.launchGame()
    }

    override fun cancelGame(): Completable {
        return cancelGameUsecase.cancelGame()
    }
}