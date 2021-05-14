package ch.qscqlmpa.dwitchgame.gamediscovery

import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchgame.di.GameScope
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchstore.store.Store
import io.reactivex.rxjava3.core.Observable
import org.joda.time.LocalTime
import org.tinylog.kotlin.Logger
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@GameScope
internal class AdvertisedGameRepository @Inject constructor(
    private val store: Store,
    private val gameDiscovery: GameDiscovery,
    private val schedulerFactory: SchedulerFactory
) {

    companion object {
        const val GAME_AD_TIMEOUT_SEC = 10
    }

    private val advertisedGames = mutableMapOf<IpAddress, AdvertisedGame>() // Local cache surviving unsubscriptions

    fun listenForAdvertisedGames(): Observable<List<AdvertisedGame>> {
        Logger.debug { "Start listening for advertised games..." }
        return Observable.combineLatest(
            resumableGames(),
            advertisedGames(),
            staleAdvertisementsCleaner(),
            { existingGames, adGame, _ -> Pair(existingGames, adGame) }
        )
            // Filter resumable games
            .filter { (existingGames, adGame) -> adGame.isNew || existingGames.contains(adGame.gameCommonId) }
            .map { (_, adGame) -> adGame }
            .doOnNext { adGame -> Logger.trace { "Game discovered: $adGame" } }
            .doOnNext { adGame -> updateLocalMap(adGame) }
            .doFinally { Logger.debug { "Stop listening for advertised games" } }
            .map { ArrayList(advertisedGames.values) }
    }

    private fun resumableGames(): Observable<List<GameCommonId>> {
        return store.getGameCommonIdOfResumableGames()
            .subscribeOn(schedulerFactory.io())
    }

    private fun advertisedGames(): Observable<AdvertisedGame> {
        return gameDiscovery.listenForAdvertisedGames()
            .subscribeOn(schedulerFactory.io())
    }

    private fun staleAdvertisementsCleaner(): Observable<Long> {
        return Observable.interval(0L, GAME_AD_TIMEOUT_SEC.toLong(), TimeUnit.SECONDS)
            .subscribeOn(schedulerFactory.io())
    }

    private fun updateLocalMap(advertisedGame: AdvertisedGame) {
        // Add new ad
        advertisedGames[IpAddress(advertisedGame.gameIpAddress)] = advertisedGame

        // Remove obsolete ads
        val timeNow = LocalTime.now()
        advertisedGames.filterValues { game -> adIsTooOld(timeNow, game) }.keys
            .forEach { ip -> advertisedGames.remove(ip) }
    }

    private fun adIsTooOld(timeNow: LocalTime, game: AdvertisedGame): Boolean {
        return timeNow.minusSeconds(GAME_AD_TIMEOUT_SEC) > game.discoveryTime
    }

    private data class IpAddress(val value: String)
}
