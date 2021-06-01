package ch.qscqlmpa.dwitchgame.home

import ch.qscqlmpa.dwitchgame.gamelifecycleevents.GuestGameLifecycleEvent
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.HostGameLifecycleEvent
import io.reactivex.rxjava3.core.Observable

interface HomeFacade {
    fun observeHostGameEvents(): Observable<HostGameLifecycleEvent>
    fun observeGuestGameEvents(): Observable<GuestGameLifecycleEvent>
    fun lastHostGameEvent(): HostGameLifecycleEvent?
    fun lastGuestGameEvent(): GuestGameLifecycleEvent?
}