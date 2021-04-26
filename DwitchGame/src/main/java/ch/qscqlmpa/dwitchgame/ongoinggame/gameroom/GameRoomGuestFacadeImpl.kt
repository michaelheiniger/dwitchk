package ch.qscqlmpa.dwitchgame.ongoinggame.gameroom

import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchgame.ongoinggame.gameevents.GuestGameEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.gameevents.GuestGameEventRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.usecases.GuestLeavesGameUsecase
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

internal class GameRoomGuestFacadeImpl @Inject constructor(
    private val gameEventRepository: GuestGameEventRepository,
    private val guestLeavesGameUsecase: GuestLeavesGameUsecase,
    private val schedulerFactory: SchedulerFactory
) : GameRoomGuestFacade {

    override fun observeEvents(): Observable<GuestGameEvent> {
        return gameEventRepository.observeEvents()
            .subscribeOn(schedulerFactory.io())
    }

    override fun consumeLastEvent(): GuestGameEvent? {
        return gameEventRepository.consumeLastEvent()
    }

    override fun leaveGame(): Completable {
        return guestLeavesGameUsecase.leaveGame()
    }
}
