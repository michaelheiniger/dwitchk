package ch.qscqlmpa.dwitchgame.ongoinggame.gameroom

import ch.qscqlmpa.dwitchgame.ongoinggame.gameevents.GuestGameEvent
import io.reactivex.rxjava3.core.Completable

interface GameRoomHostFacade {
    fun consumeLastEvent(): GuestGameEvent?
    fun endGame(): Completable
}
