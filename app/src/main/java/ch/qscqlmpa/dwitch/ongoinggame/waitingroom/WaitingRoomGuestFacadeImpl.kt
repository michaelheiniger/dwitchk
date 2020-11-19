package ch.qscqlmpa.dwitch.ongoinggame.waitingroom

import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.events.GuestCommunicationState
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GuestGameEvent
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GuestGameEventRepository
import ch.qscqlmpa.dwitch.ongoinggame.usecases.LeaveGameUsecase
import ch.qscqlmpa.dwitch.ongoinggame.usecases.PlayerReadyUsecase
import io.reactivex.Completable
import io.reactivex.Observable
import javax.inject.Inject

internal class WaitingRoomGuestFacadeImpl @Inject constructor(
    private val guestCommunicator: GuestCommunicator,
    private val playerReadyUsecase: PlayerReadyUsecase,
    private val leaveGameUsecase: LeaveGameUsecase,
    private val wrPlayerRepository: WaitingRoomPlayerRepository,
    private val gameEventRepository: GuestGameEventRepository
) : WaitingRoomGuestFacade{

    override fun connect() {
        guestCommunicator.connect()
    }

    override fun observeCommunicationState(): Observable<GuestCommunicationState> {
        return guestCommunicator.observeCommunicationState()
    }

    override fun updateReadyState(ready: Boolean): Completable {
        return playerReadyUsecase.updateReadyState(ready)
    }

    override fun leaveGame(): Completable {
        return leaveGameUsecase.leaveGame()
    }

    override fun observeLocalPlayerReadyState(): Observable<Boolean> {
        return wrPlayerRepository.observeLocalPlayer().map(PlayerWr::ready)
    }

    override fun observeEvents(): Observable<GuestGameEvent> {
        return gameEventRepository.observeEvents()
    }
}