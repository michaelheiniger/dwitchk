package ch.qscqlmpa.dwitchgame.ingame.waitingroom

import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationState
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationStateRepository
import ch.qscqlmpa.dwitchgame.ingame.gameevents.GuestGameEvent
import ch.qscqlmpa.dwitchgame.ingame.gameevents.GuestGameEventRepository
import ch.qscqlmpa.dwitchgame.ingame.usecases.GuestLeavesGameUsecase
import ch.qscqlmpa.dwitchgame.ingame.usecases.PlayerReadyUsecase
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

internal class WaitingRoomGuestFacadeImpl @Inject constructor(
    private val communicationStateRepository: GuestCommunicationStateRepository,
    private val playerReadyUsecase: PlayerReadyUsecase,
    private val guestLeavesGameUsecase: GuestLeavesGameUsecase,
    private val wrPlayerRepository: WaitingRoomPlayerRepository,
    private val gameEventRepository: GuestGameEventRepository,
    private val schedulerFactory: SchedulerFactory
) : WaitingRoomGuestFacade {

    override fun updateReadyState(ready: Boolean): Completable {
        return playerReadyUsecase.updateReadyState(ready)
            .subscribeOn(schedulerFactory.io())
    }

    override fun leaveGame(): Completable {
        return guestLeavesGameUsecase.leaveGame()
            .subscribeOn(schedulerFactory.io())
    }

    override fun observeCommunicationState(): Observable<GuestCommunicationState> {
        return communicationStateRepository.currentState()
    }

    override fun observeLocalPlayerReadyState(): Observable<Boolean> {
        return wrPlayerRepository.observeLocalPlayer()
            .map(PlayerWrUi::ready)
            .subscribeOn(schedulerFactory.io())
    }

    override fun observeGameEvents(): Observable<GuestGameEvent> {
        return gameEventRepository.observeEvents()
            .subscribeOn(schedulerFactory.io())
    }
}
