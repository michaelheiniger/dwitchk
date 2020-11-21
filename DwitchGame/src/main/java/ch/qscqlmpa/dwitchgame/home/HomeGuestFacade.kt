package ch.qscqlmpa.dwitchgame.home

import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame
import io.reactivex.Completable

interface HomeGuestFacade {

    fun joinGame(advertisedGame: AdvertisedGame, playerName: String): Completable
}