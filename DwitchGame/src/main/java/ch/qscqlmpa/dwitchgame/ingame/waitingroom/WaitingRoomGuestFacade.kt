package ch.qscqlmpa.dwitchgame.ingame.waitingroom

import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface WaitingRoomGuestFacade {

    /**
     * Updates the ready state of the local player: [ready] == true means that the guests is ready for the game to start, [ready] == false means the opposite.
     * The host may only start the game when all players are ready.
     */
    fun updateReadyState(ready: Boolean): Completable

    /**
     * Emit the current ready state of the local player.
     */
    fun observeLocalPlayerReadyState(): Observable<Boolean>
}
