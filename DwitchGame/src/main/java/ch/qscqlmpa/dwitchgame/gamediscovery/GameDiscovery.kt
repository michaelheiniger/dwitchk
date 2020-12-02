package ch.qscqlmpa.dwitchgame.gamediscovery

import io.reactivex.rxjava3.core.Observable


interface GameDiscovery {

    fun listenForAdvertisedGame(): Observable<AdvertisedGame>

    fun stopListening()
}
