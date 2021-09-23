package ch.qscqlmpa.dwitchgame.gamediscovery

import ch.qscqlmpa.dwitchcommunication.GameAdvertisingInfo
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
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
    fun observeAdvertisedGames(): Observable<List<GameAdvertisingInfo>>

    /**
     * Get advertised game using its game common ID.
     * Returns null if the advertisement is obsolete.
     */
    fun getAdvertisedGame(gameCommonId: GameCommonId): GameAdvertisingInfo?
}
