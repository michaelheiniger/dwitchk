package ch.qscqlmpa.dwitchgame.ongoinggame.gameroom

import ch.qscqlmpa.dwitchgame.ongoinggame.game.events.GuestGameEvent
import io.reactivex.rxjava3.core.Observable

interface GameRoomGuestFacade {
    fun consumeLastEvent(): GuestGameEvent?
    fun observeEvents(): Observable<GuestGameEvent>
}
