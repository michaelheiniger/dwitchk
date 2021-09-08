package ch.qscqlmpa.dwitchgame.gameadvertising

import io.reactivex.rxjava3.core.Completable

interface GameAdvertisingFacade {
    fun advertiseGame(gameAdvertisingInfo: GameAdvertisingInfo): Completable
}