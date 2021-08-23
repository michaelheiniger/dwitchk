package ch.qscqlmpa.dwitchgame.gameadvertising

import io.reactivex.rxjava3.core.Completable

internal interface GameAdvertising {
    fun advertiseGame(gameAdvertisingInfo: GameAdvertisingInfo): Completable
}
