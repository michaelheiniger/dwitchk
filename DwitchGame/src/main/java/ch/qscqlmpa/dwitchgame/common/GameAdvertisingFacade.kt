package ch.qscqlmpa.dwitchgame.common

import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame
import io.reactivex.rxjava3.core.Observable

interface GameAdvertisingFacade {

    /**
     * Listen for advertised games and store them in the repository until they become obsolete.
     *
     * **This method is idempotent.**
     */
    fun startListeningForAdvertisedGames()

    /**
     * Stop listen for advertised games and clears the store of game ads.
     *
     * **This method is idempotent.**
     */
    fun stopListeningForAdvertisedGames()

    /**
     * Stream the list of advertised games in the repository.
     */
    fun observeAdvertisedGames(): Observable<List<AdvertisedGame>>
}
