package ch.qscqlmpa.dwitch.ongoinggame.gameroom

import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GuestGameEvent
import io.reactivex.Completable

interface GameRoomHostFacade {

    fun consumeLastEvent(): GuestGameEvent?

    fun endGame(): Completable
}