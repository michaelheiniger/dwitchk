package ch.qscqlmpa.dwitchgame.gameadvertising

import ch.qscqlmpa.dwitchmodel.gamediscovery.GameAdvertisingInfo
import io.reactivex.rxjava3.core.Completable

interface GameAdvertising {

    fun advertiseGame(gameAdvertisingInfo: GameAdvertisingInfo): Completable
}