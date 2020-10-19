package ch.qscqlmpa.dwitch.gamediscovery

import ch.qscqlmpa.dwitch.gamediscovery.AdvertisedGame
import io.reactivex.Observable

interface GameDiscovery {

    fun listenForAdvertisedGame(): Observable<AdvertisedGame>

    fun stopListening()
}
