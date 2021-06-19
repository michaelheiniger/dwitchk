package ch.qscqlmpa.dwitchgame.ingame.gameroom

import ch.qscqlmpa.dwitchgame.ingame.gameevents.GuestGameEvent
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface GameRoomGuestFacade {
    fun observeGameEvents(): Observable<GuestGameEvent>
    fun leaveGame(): Completable
}
