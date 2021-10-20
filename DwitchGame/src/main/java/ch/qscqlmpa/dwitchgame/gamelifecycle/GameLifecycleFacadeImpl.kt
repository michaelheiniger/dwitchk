package ch.qscqlmpa.dwitchgame.gamelifecycle

import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchstore.store.Store
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import javax.inject.Inject

internal class GameLifecycleFacadeImpl @Inject constructor(
    private val store: Store,
    private val gameStateRepository: GameStateRepository,
    private val hostGameLifecycleEventRepository: HostGameLifecycleEventRepository,
    private val guestGameLifecycleEventRepository: GuestGameLifecycleEventRepository,
    private val schedulerFactory: SchedulerFactory
) : GameLifecycleFacade {

    override val currentLifecycleState: GameLifecycleState
        get() = gameStateRepository.lifecycleState

    override fun observeHostEvents(): Observable<HostGameLifecycleEvent> {
        return hostGameLifecycleEventRepository.observeEvents()
    }

    override fun observeGuestEvents(): Observable<GuestGameLifecycleEvent> {
        return guestGameLifecycleEventRepository.observeEvents()
    }

    override fun cleanUpGameResources(): Completable {
        return Completable.fromAction {
            gameStateRepository.reset()
            store.deleteGamesMarkedForDeletion()
        }
            .subscribeOn(schedulerFactory.io())
    }
}
