package ch.qscqlmpa.dwitch.ongoinggame.waitingroom

import ch.qscqlmpa.dwitch.ongoinggame.events.GuestCommunicationState
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GuestGameEvent
import io.reactivex.Completable
import io.reactivex.Observable

interface WaitingRoomGuestFacade {

    fun connect()

    fun updateReadyState(ready: Boolean): Completable

    fun leaveGame(): Completable

    fun observeCommunicationState(): Observable<GuestCommunicationState>

    fun observeLocalPlayerReadyState(): Observable<Boolean>

    fun observeEvents(): Observable<GuestGameEvent>
}