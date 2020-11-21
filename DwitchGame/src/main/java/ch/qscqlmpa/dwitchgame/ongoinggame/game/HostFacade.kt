package ch.qscqlmpa.dwitchgame.ongoinggame.game

import ch.qscqlmpa.dwitchmodel.gamediscovery.GameAdvertisingInfo
import io.reactivex.Completable

interface HostFacade {
    fun listenForConnections()
    fun closeAllConnections()
    fun advertiseGame(gameAdvertisingInfo: GameAdvertisingInfo): Completable
}