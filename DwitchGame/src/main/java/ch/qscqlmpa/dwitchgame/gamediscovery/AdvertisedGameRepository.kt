package ch.qscqlmpa.dwitchgame.gamediscovery

import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchgame.di.GameScope
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchstore.store.Store
import io.reactivex.rxjava3.core.Observable
import mu.KLogging
import org.joda.time.LocalTime
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@GameScope
internal class AdvertisedGameRepository @Inject constructor(
    private val store: Store,
    private val gameDiscovery: GameDiscovery,
    private val schedulerFactory: SchedulerFactory
) {

    companion object : KLogging() {
        const val GAME_AD_TIMEOUT_SEC = 10
    }

    private val advertisedGames = mutableMapOf<IpAddress, AdvertisedGame>() // Local cache surviving unsubscriptions

    fun listenForAdvertisedGames(): Observable<List<AdvertisedGame>> {
        return Observable.combineLatest(
            resumableGames(),
            advertisedGames(),
            staleAdvertisementsCleaner(),
            { existingGames, adGame, _ -> Pair(existingGames, adGame) }
        )
            // Filter resumable games
            .filter { (existingGames, adGame) -> adGame.isNew || existingGames.contains(adGame.gameCommonId) }
            .map { (_, adGame) -> adGame }
            .doOnNext { adGame -> logger.trace { "Game discovered: $adGame" } }
            .doOnNext { adGame -> updateLocalMap(adGame) }
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

    private fun updateLocalMap(advertisedGame: AdvertisedGame): Map<IpAddress, AdvertisedGame> {
        val timeNow = LocalTime.now()
        advertisedGames[IpAddress(advertisedGame.gameIpAddress)] = advertisedGame
        return advertisedGames.filterValues { game -> gameAdIsRecentEnough(timeNow, game) }
    }

    private fun gameAdIsRecentEnough(timeNow: LocalTime, game: AdvertisedGame): Boolean {
        return timeNow.minusSeconds(GAME_AD_TIMEOUT_SEC) <= game.discoveryTime
    }

    private data class IpAddress(val value: String)
}
