package ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom

import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicationState
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitchgame.ongoinggame.game.events.GuestGameEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.game.events.GuestGameEventRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.usecases.LeaveGameUsecase
import ch.qscqlmpa.dwitchgame.ongoinggame.usecases.PlayerReadyUsecase
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

internal class WaitingRoomGuestFacadeImpl @Inject constructor(
    private val guestCommunicator: GuestCommunicator,
    private val playerReadyUsecase: PlayerReadyUsecase,
    private val leaveGameUsecase: LeaveGameUsecase,
    private val wrPlayerRepository: WaitingRoomPlayerRepository,
    private val gameEventRepository: GuestGameEventRepository
) : WaitingRoomGuestFacade {

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