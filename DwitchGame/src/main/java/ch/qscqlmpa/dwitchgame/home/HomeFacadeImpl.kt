package ch.qscqlmpa.dwitchgame.home

import ch.qscqlmpa.dwitchgame.gamelifecycleevents.GuestGameLifecycleEvent
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.GuestGameLifecycleEventRepository
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.HostGameLifecycleEvent
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.HostGameLifecycleEventRepository
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

internal class HomeFacadeImpl @Inject constructor(
    private val hostGameLifecycleEventRepository: HostGameLifecycleEventRepository,
    private val guestGameLifecycleEventRepository: GuestGameLifecycleEventRepository
) : HomeFacade {

    override fun observeHostGameEvents(): Observable<HostGameLifecycleEvent> {
        return hostGameLifecycleEventRepository.observeEvents()
    }

    override fun observeGuestGameEvents(): Observable<GuestGameLifecycleEvent> {
        return guestGameLifecycleEventRepository.observeEvents()
    }

    override fun lastHostGameEvent(): HostGameLifecycleEvent? {
        return hostGameLifecycleEventRepository.lastEvent()
    }

    override fun lastGuestGameEvent(): GuestGameLifecycleEvent? {
        return guestGameLifecycleEventRepository.lastEvent()
    }
}