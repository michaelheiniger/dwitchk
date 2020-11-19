package ch.qscqlmpa.dwitch.ongoinggame.gameroom

import io.reactivex.Completable

interface GameRoomHostFacade {

    fun endGame(): Completable
}