package ch.qscqlmpa.dwitchgame.ingame.gameroom

import io.reactivex.rxjava3.core.Completable

interface GameRoomHostFacade {
    fun endGame(): Completable
}
