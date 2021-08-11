package ch.qscqlmpa.dwitchgame.home

import ch.qscqlmpa.dwitchgame.gamelifecycleevents.GuestGameLifecycleEvent
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.HostGameLifecycleEvent
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface HomeFacade {
    val gameRunning: Boolean
    fun observeHostGameEvents(): Observable<HostGameLifecycleEvent>
    fun observeGuestGameEvents(): Observable<GuestGameLifecycleEvent>
    fun deleteGamesMarkedForDeletion(): Completable
}
