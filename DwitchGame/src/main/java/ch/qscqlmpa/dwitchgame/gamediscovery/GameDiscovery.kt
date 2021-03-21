package ch.qscqlmpa.dwitchgame.gamediscovery

import io.reactivex.rxjava3.core.Observable

interface GameDiscovery {
    fun listenForAdvertisedGames(): Observable<AdvertisedGame>
}
