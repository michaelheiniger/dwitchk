package ch.qscqlmpa.dwitchgame.ongoinggame.gameroom

import io.reactivex.rxjava3.core.Completable

interface GameRoomHostFacade {
    fun endGame(): Completable
}
