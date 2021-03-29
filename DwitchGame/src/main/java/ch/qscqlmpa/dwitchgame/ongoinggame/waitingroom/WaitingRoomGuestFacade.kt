package ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom

import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicationState
import ch.qscqlmpa.dwitchgame.ongoinggame.game.events.GuestGameEvent
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface WaitingRoomGuestFacade {
    fun connect()
    fun updateReadyState(ready: Boolean): Completable
    fun leaveGame(): Completable
    fun observeCommunicationState(): Observable<GuestCommunicationState>
    fun observeLocalPlayerReadyState(): Observable<Boolean>
    fun observeGameEvents(): Observable<GuestGameEvent>
}
