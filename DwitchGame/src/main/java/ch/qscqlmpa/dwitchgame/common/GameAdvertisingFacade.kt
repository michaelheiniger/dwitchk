package ch.qscqlmpa.dwitchgame.common

import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame
import io.reactivex.rxjava3.core.Observable

interface GameAdvertisingFacade {
    fun startListeningForAdvertisedGames()
    fun stopListeningForAdvertisedGames()
    fun observeAdvertisedGames(): Observable<List<AdvertisedGame>>
}
