package ch.qscqlmpa.dwitchcommunication.gameadvertising

import ch.qscqlmpa.dwitchcommunication.GameInfo
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface GameAdvertiser {
    fun observeSerializedGameAdvertisingInfo(gameInfo: GameInfo): Observable<AdvertisingInfo>
    fun advertiseGame(gameInfo: GameInfo): Completable
}

sealed class AdvertisingInfo {
    data class Info(val serializedAd: String) : AdvertisingInfo()
    object NoInfoAvailable : AdvertisingInfo()
}
