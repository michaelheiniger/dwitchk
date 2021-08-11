package ch.qscqlmpa.dwitchgame.ingame.gameroom

import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchgame.ingame.gameevents.GuestGameEvent
import ch.qscqlmpa.dwitchgame.ingame.gameevents.GuestGameEventRepository
import ch.qscqlmpa.dwitchgame.ingame.usecases.GuestLeavesGameUsecase
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

internal class GameRoomGuestFacadeImpl @Inject constructor(
    private val gameEventRepository: GuestGameEventRepository,
    private val guestLeavesGameUsecase: GuestLeavesGameUsecase,
    private val schedulerFactory: SchedulerFactory
) : GameRoomGuestFacade {

    override fun observeGameEvents(): Observable<GuestGameEvent> {
        return gameEventRepository.observeEvents()
            .subscribeOn(schedulerFactory.io())
    }

    override fun leaveGame(): Completable {
        return guestLeavesGameUsecase.leaveGame()
            .subscribeOn(schedulerFactory.io())
    }
}