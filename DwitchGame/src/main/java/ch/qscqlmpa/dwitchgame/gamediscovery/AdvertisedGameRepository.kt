package ch.qscqlmpa.dwitchgame.gamediscovery

import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchgame.di.GameScope
import io.reactivex.rxjava3.core.Observable
import org.joda.time.LocalTime
import timber.log.Timber
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@GameScope
class AdvertisedGameRepository @Inject constructor(
    private val gameDiscovery: GameDiscovery,
    private val schedulerFactory: SchedulerFactory
) {

    companion object {
        const val GAME_AD_TIMEOUT_SEC = 5
    }

    fun listenForAdvertisedGames(): Observable<List<AdvertisedGame>> {
        return Observable.combineLatest(
            advertisedGames(),
            cleanUpScheduler(),
            { game, _ -> game }
        )
            .doOnNext { game -> Timber.v("New game discovered: $game") }
            .scan(
                mapOf<String, AdvertisedGame>(),
                { mapOfGames, game -> buildUpdatedMap(mapOfGames, game) }
            )
            .map { mapOfGames -> ArrayList(mapOfGames.values) }
    }

    private fun advertisedGames(): Observable<AdvertisedGame> {
        return gameDiscovery.listenForAdvertisedGame()
            .subscribeOn(schedulerFactory.io())
    }

    fun stopListening() {
        gameDiscovery.stopListening()
    }

    private fun cleanUpScheduler(): Observable<Long> {
        return Observable.interval(0L, GAME_AD_TIMEOUT_SEC.toLong(), TimeUnit.SECONDS)
            .subscribeOn(schedulerFactory.io())
    }

    private fun buildUpdatedMap(
        map: Map<String, AdvertisedGame>,
        advertisedGame: AdvertisedGame
    ): Map<String, AdvertisedGame> {
        val timeNow = LocalTime.now()
        val mutableMap = map.toMutableMap()
        mutableMap[advertisedGame.gameIpAddress] = advertisedGame
        return mutableMap.filterValues { game -> gameAdIsRecentEnough(timeNow, game) }
    }

    private fun gameAdIsRecentEnough(timeNow: LocalTime, game: AdvertisedGame): Boolean {
        return timeNow.minusSeconds(GAME_AD_TIMEOUT_SEC) <= game.discoveryTime
    }
}
