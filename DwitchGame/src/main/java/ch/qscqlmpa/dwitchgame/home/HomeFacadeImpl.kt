package ch.qscqlmpa.dwitchgame.home

import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.GuestGameLifecycleEvent
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.GuestGameLifecycleEventRepository
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.HostGameLifecycleEvent
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.HostGameLifecycleEventRepository
import ch.qscqlmpa.dwitchstore.store.Store
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

internal class HomeFacadeImpl @Inject constructor(
    private val store: Store,
    private val hostGameLifecycleEventRepository: HostGameLifecycleEventRepository,
    private val guestGameLifecycleEventRepository: GuestGameLifecycleEventRepository,
    private val schedulerFactory: SchedulerFactory
) : HomeFacade {

    override val gameRunning: Boolean
        get() = hostGameLifecycleEventRepository.gameRunning || guestGameLifecycleEventRepository.gameRunning

    override fun observeHostGameEvents(): Observable<HostGameLifecycleEvent> {
        return hostGameLifecycleEventRepository.observeEvents()
    }

    override fun observeGuestGameEvents(): Observable<GuestGameLifecycleEvent> {
        return guestGameLifecycleEventRepository.observeEvents()
    }

    override fun deleteGamesMarkedForDeletion(): Completable {
        return Completable.fromAction { store.deleteGamesMarkedForDeletion() }
            .subscribeOn(schedulerFactory.io())
    }
}
