package ch.qscqlmpa.dwitch.gamediscovery

import io.reactivex.Observable
import io.reactivex.functions.BiFunction
import org.joda.time.LocalTime
import timber.log.Timber
import java.util.*
import java.util.concurrent.TimeUnit
import javax.inject.Inject

class AdvertisedGameRepository @Inject
constructor(private val gameDiscovery: GameDiscovery) {

    companion object {
        const val GAME_AD_TIMEOUT_SEC = 15
    }

    fun listenForAdvertisedGames(): Observable<List<AdvertisedGame>> {
        return Observable.combineLatest<AdvertisedGame, Long, AdvertisedGame>(
                gameDiscovery.listenForAdvertisedGame(),
                Observable.interval(GAME_AD_TIMEOUT_SEC.toLong(), TimeUnit.SECONDS)
                        .startWith(0),
                BiFunction { game, _ -> game }
        )
                .doOnNext { game -> Timber.i("New game discovered: %s ", game.toString()) }
                .scan(HashMap<String, AdvertisedGame>(), { mapOfGames, game -> buildUpdatedMap(mapOfGames, game) })
                .map { mapOfGames -> ArrayList(mapOfGames.values) }
    }

    fun stopListening() {
        gameDiscovery.stopListening()
    }

    private fun buildUpdatedMap(map: HashMap<String, AdvertisedGame>, advertisedGame: AdvertisedGame): HashMap<String, AdvertisedGame> {
        val timeNow = LocalTime.now()
        map[advertisedGame.ipAddress] = advertisedGame
        return HashMap(map.filterValues { game ->
            timeNow.minusSeconds(GAME_AD_TIMEOUT_SEC).compareTo(game.discoveryTime) <=
                    1
        })
    }
}
