package ch.qscqlmpa.dwitchgame.home

import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface HomeGuestFacade {

    fun listenForAdvertisedGames(): Observable<List<AdvertisedGame>>

    fun getAdvertisedGame(ipAddress: String): AdvertisedGame?

    fun joinGame(advertisedGame: AdvertisedGame, playerName: String): Completable

    fun joinResumedGame(advertisedGame: AdvertisedGame): Completable
}
