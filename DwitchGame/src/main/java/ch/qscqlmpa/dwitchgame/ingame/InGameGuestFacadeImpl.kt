package ch.qscqlmpa.dwitchgame.ingame

import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchgame.ingame.gameevents.GuestGameEvent
import ch.qscqlmpa.dwitchgame.ingame.gameevents.GuestGameEventRepository
import ch.qscqlmpa.dwitchgame.ingame.usecases.GuestLeavesGameUsecase
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

internal class InGameGuestFacadeImpl @Inject constructor(
    private val guestLeavesGameUsecase: GuestLeavesGameUsecase,
    private val gameEventRepository: GuestGameEventRepository,
    private val schedulerFactory: SchedulerFactory
) : InGameGuestFacade {

    override fun leaveGame(): Completable {
        return guestLeavesGameUsecase.leaveGame()
            .subscribeOn(schedulerFactory.io())
    }

    override fun observeGameEvents(): Observable<GuestGameEvent> {
        return gameEventRepository.observeEvents()
            .subscribeOn(schedulerFactory.io())
    }
}
