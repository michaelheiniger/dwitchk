package ch.qscqlmpa.dwitchgame.gamediscovery

import io.reactivex.Observable

interface GameDiscovery {

    fun listenForAdvertisedGame(): Observable<AdvertisedGame>

    fun stopListening()
}
