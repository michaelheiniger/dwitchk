package ch.qscqlmpa.dwitchgame.ingame

import ch.qscqlmpa.dwitchgame.ingame.gameevents.GuestGameEvent
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface InGameGuestFacade {

    /**
     * The local player leaves the current game.
     * Leaving a game that has been started and is on-going will prevent the other players to play the game because all players must be connected.
     */
    fun leaveGame(): Completable

    /**
     * Emits the last game event.
     */
    fun observeGameEvents(): Observable<GuestGameEvent>
}