package ch.qscqlmpa.dwitchgame.home

import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.*
import ch.qscqlmpa.dwitchstore.store.Store
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

internal class HomeFacadeImpl @Inject constructor(
    private val store: Store,
    private val gameStateRepository: GameStateRepository,
    private val hostGameLifecycleEventRepository: HostGameLifecycleEventRepository,
    private val guestGameLifecycleEventRepository: GuestGameLifecycleEventRepository,
    private val schedulerFactory: SchedulerFactory
) : HomeFacade {

    override val gameState: GameState
        get() = gameStateRepository.state

    override fun observeHostGameEvents(): Observable<HostGameLifecycleEvent> {
        return hostGameLifecycleEventRepository.observeEvents()
    }

    override fun observeGuestGameEvents(): Observable<GuestGameLifecycleEvent> {
        return guestGameLifecycleEventRepository.observeEvents()
    }

    override fun reset(): Completable {
        return Completable.fromAction {
            store.deleteGamesMarkedForDeletion()
            gameStateRepository.reset()
        }
            .subscribeOn(schedulerFactory.io())
    }
}
