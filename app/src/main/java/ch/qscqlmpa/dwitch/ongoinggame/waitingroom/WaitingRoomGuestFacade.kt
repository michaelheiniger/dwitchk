package ch.qscqlmpa.dwitch.ongoinggame.waitingroom

import ch.qscqlmpa.dwitch.ongoinggame.GuestFacade
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GuestGameEvent
import io.reactivex.Completable
import io.reactivex.Observable

interface WaitingRoomGuestFacade : GuestFacade {

    fun updateReadyState(ready: Boolean): Completable

    fun leaveGame(): Completable

    fun observeLocalPlayerReadyState(): Observable<Boolean>

    fun observeEvents(): Observable<GuestGameEvent>
}