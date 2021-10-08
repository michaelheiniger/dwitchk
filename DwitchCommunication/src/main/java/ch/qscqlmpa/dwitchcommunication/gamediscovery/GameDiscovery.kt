package ch.qscqlmpa.dwitchcommunication.gamediscovery

import ch.qscqlmpa.dwitchcommunication.GameAdvertisingInfo
import io.reactivex.rxjava3.core.Observable

interface GameDiscovery {
    fun listenForAdvertisedGames(): Observable<GameAdvertisingInfo>
    fun deserializeGameAdvertisingInfo(str: String): GameAdvertisingInfo?
}
