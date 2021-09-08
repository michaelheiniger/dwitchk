package ch.qscqlmpa.dwitchgame.gamediscovery

import ch.qscqlmpa.dwitchgame.gameadvertising.AdvertisedGame
import io.reactivex.rxjava3.core.Observable

interface GameDiscoveryFacade {

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

    /**
     * Get advertised game using its IP address.
     * Returns null if the advertisement is obsolete.
     */
    fun getAdvertisedGame(ipAddress: String): AdvertisedGame?
}
