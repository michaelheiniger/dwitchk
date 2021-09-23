package ch.qscqlmpa.dwitchgame.ingame.gameadvertising

import ch.qscqlmpa.dwitchcommunication.gameadvertising.AdvertisingInfo
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface GameAdvertisingFacade {
    fun advertiseGame(): Completable
    fun observeAdvertisingInfo(): Observable<AdvertisingInfo>
}
