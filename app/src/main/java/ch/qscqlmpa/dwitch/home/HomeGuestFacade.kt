package ch.qscqlmpa.dwitch.home

import ch.qscqlmpa.dwitch.gamediscovery.AdvertisedGame
import io.reactivex.Completable

interface HomeGuestFacade {

    fun joinGame(advertisedGame: AdvertisedGame, playerName: String): Completable
}