package ch.qscqlmpa.dwitch.gamediscovery

import io.reactivex.Observable

interface GameDiscovery {

    fun listenForAdvertisedGame(): Observable<AdvertisedGame>

    fun stopListening()
}
