package ch.qscqlmpa.dwitch.ongoinggame.gameroom

import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GuestGameEvent
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GuestGameEventRepository
import io.reactivex.Observable
import javax.inject.Inject

internal class GameRoomGuestFacadeImpl @Inject constructor(
    private val gameEventRepository: GuestGameEventRepository
) : GameRoomGuestFacade {

    override fun observeEvents(): Observable<GuestGameEvent> {
        return gameEventRepository.observeEvents()
    }

    override fun consumeLastEvent(): GuestGameEvent? {
        return gameEventRepository.consumeLastEvent()
    }
}