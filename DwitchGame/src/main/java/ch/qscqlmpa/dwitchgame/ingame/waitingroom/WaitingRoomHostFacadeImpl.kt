package ch.qscqlmpa.dwitchgame.ingame.waitingroom

import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchgame.ingame.usecases.*
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

internal class WaitingRoomHostFacadeImpl @Inject constructor(
    private val gameLaunchableUsecase: GameLaunchableUsecase,
    private val addComputerPlayerUsecase: AddComputerPlayerUsecase,
    private val kickPlayerUsecase: KickPlayerUsecase,
    private val launchGameUsecase: LaunchGameUsecase,
    private val cancelGameUsecase: CancelGameUsecase,
    private val schedulerFactory: SchedulerFactory
) : WaitingRoomHostFacade {

    override fun addComputerPlayer(): Completable {
        return addComputerPlayerUsecase.addPlayer()
    }

    override fun kickPlayer(player: PlayerWrUi): Completable {
        return kickPlayerUsecase.kickPlayer(player)
            .subscribeOn(schedulerFactory.io())
    }

    override fun cancelGame(): Completable {
        return cancelGameUsecase.cancelGame()
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
}
