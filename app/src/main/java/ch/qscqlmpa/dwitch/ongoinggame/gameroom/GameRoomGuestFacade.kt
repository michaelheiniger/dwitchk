package ch.qscqlmpa.dwitch.ongoinggame.gameroom

import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GuestGameEvent
import io.reactivex.Observable

interface GameRoomGuestFacade {

    fun observeEvents(): Observable<GuestGameEvent>

    fun consumeLastEvent(): GuestGameEvent?
}