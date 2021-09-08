package ch.qscqlmpa.dwitchgame.gamelifecycle

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface GameLifecycleFacade {

    /**
     * Current state of the game lifecycle.
     */
    val currentLifecycleState: GameLifecycleState

    /**
     * Emits lifecycle events for the current game when the local player is the host.
     */
    fun observeHostEvents(): Observable<HostGameLifecycleEvent>

    /**
     * Emits lifecycle events for the current game when the local player is a guest.
     */
    fun observeGuestEvents(): Observable<GuestGameLifecycleEvent>

    /**
     * Clean-up resources created for the game. To be called when the game is canceled or ended.
     */
    fun cleanUpGameResources(): Completable
}
