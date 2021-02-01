package ch.qscqlmpa.dwitchgame.ongoinggame.gameroom

import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchgame.ongoinggame.game.events.GuestGameEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.game.events.GuestGameEventRepository
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

internal class GameRoomGuestFacadeImpl @Inject constructor(
    private val gameEventRepository: GuestGameEventRepository,
    private val schedulerFactory: SchedulerFactory
) : GameRoomGuestFacade {

    override fun observeEvents(): Observable<GuestGameEvent> {
        return gameEventRepository.observeEvents()
            .subscribeOn(schedulerFactory.io())
    }

    override fun consumeLastEvent(): GuestGameEvent? {
        return gameEventRepository.consumeLastEvent()
    }
}