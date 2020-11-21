package ch.qscqlmpa.dwitchgame.ongoinggame.gameroom

import ch.qscqlmpa.dwitchgame.ongoinggame.game.events.GuestGameEvent
import io.reactivex.Observable

interface GameRoomGuestFacade {

    fun observeEvents(): Observable<GuestGameEvent>

    fun consumeLastEvent(): GuestGameEvent?
}