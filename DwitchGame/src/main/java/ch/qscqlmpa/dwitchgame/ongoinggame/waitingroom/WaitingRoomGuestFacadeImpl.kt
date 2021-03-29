package ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom

import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicationState
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.game.events.GuestGameEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.game.events.GuestGameEventRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.usecases.LeaveGameUsecase
import ch.qscqlmpa.dwitchgame.ongoinggame.usecases.PlayerReadyUsecase
import ch.qscqlmpa.dwitchmodel.player.PlayerWr
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

internal class WaitingRoomGuestFacadeImpl @Inject constructor(
    private val guestCommunicator: GuestCommunicator,
    private val playerReadyUsecase: PlayerReadyUsecase,
    private val leaveGameUsecase: LeaveGameUsecase,
    private val wrPlayerRepository: WaitingRoomPlayerRepository,
    private val gameEventRepository: GuestGameEventRepository,
    private val schedulerFactory: SchedulerFactory
) : WaitingRoomGuestFacade {

    override fun connect() {
        guestCommunicator.connect()
    }

    override fun observeCommunicationState(): Observable<GuestCommunicationState> {
        return guestCommunicator.currentCommunicationState()
            .subscribeOn(schedulerFactory.io())
    }

    override fun updateReadyState(ready: Boolean): Completable {
        return playerReadyUsecase.updateReadyState(ready)
            .subscribeOn(schedulerFactory.io())
    }

    override fun leaveGame(): Completable {
        return leaveGameUsecase.leaveGame()
            .subscribeOn(schedulerFactory.io())
    }

    override fun observeLocalPlayerReadyState(): Observable<Boolean> {
        return wrPlayerRepository.observeLocalPlayer()
            .map(PlayerWr::ready)
            .subscribeOn(schedulerFactory.io())
    }

    override fun observeGameEvents(): Observable<GuestGameEvent> {
        return gameEventRepository.observeEvents()
            .subscribeOn(schedulerFactory.io())
    }
}
