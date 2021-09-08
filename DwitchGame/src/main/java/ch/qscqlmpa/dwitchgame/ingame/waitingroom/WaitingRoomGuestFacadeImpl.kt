package ch.qscqlmpa.dwitchgame.ingame.waitingroom

import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchgame.ingame.usecases.PlayerReadyUsecase
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

internal class WaitingRoomGuestFacadeImpl @Inject constructor(
    private val playerReadyUsecase: PlayerReadyUsecase,
    private val playerRepository: WaitingRoomPlayerRepository,
    private val schedulerFactory: SchedulerFactory
) : WaitingRoomGuestFacade {

    override fun updateReadyState(ready: Boolean): Completable {
        return playerReadyUsecase.updateReadyState(ready)
            .subscribeOn(schedulerFactory.io())
    }

    override fun observeLocalPlayerReadyState(): Observable<Boolean> {
        return playerRepository.observeLocalPlayer()
            .map(PlayerWrUi::ready)
            .subscribeOn(schedulerFactory.io())
    }
}
