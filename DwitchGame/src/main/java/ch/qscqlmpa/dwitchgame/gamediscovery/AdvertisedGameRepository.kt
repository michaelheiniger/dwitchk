package ch.qscqlmpa.dwitchgame.gamediscovery

import ch.qscqlmpa.dwitchcommonutil.DisposableManager
import ch.qscqlmpa.dwitchcommonutil.DwitchIdlingResource
import ch.qscqlmpa.dwitchcommonutil.TimeProvider
import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchgame.di.GameScope
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchstore.store.Store
import com.jakewharton.rxrelay3.BehaviorRelay
import io.reactivex.rxjava3.core.Observable
import org.joda.time.LocalDateTime
import org.tinylog.kotlin.Logger
import java.util.concurrent.TimeUnit
import javax.inject.Inject

@GameScope
internal class AdvertisedGameRepository @Inject constructor(
    private val store: Store,
    private val gameDiscovery: GameDiscovery,
    private val schedulerFactory: SchedulerFactory,
    private val timeProvider: TimeProvider,
    private val idlingResource: DwitchIdlingResource
) {

    companion object {
        const val GAME_AD_TIMEOUT_SEC = 4
    }

    private val disposableManager = DisposableManager()
    private val advertisedGamesRelay = BehaviorRelay.createDefault<List<AdvertisedGame>>(emptyList())
    private val advertisedGames = mutableMapOf<IpAddress, AdvertisedGame>() // Local cache surviving unsubscriptions

    fun startListeningForAdvertisedGames() {
        Logger.debug { "Start listening for advertised games..." }
        disposableManager.add(
            Observable.combineLatest(
                resumableGames(),
                advertisedGames(),
                staleAdvertisementsCleaner(),
                { existingGames, adGame, _ -> Pair(existingGames, adGame) }
            )
                // Filter resumable games
                .filter { (resumableGames, adGame) -> adGame.isNew || resumableGames.contains(adGame.gameCommonId) }
                .map { (_, adGame) -> adGame }
                .doFinally {
                    advertisedGamesRelay.accept(emptyList())
                    advertisedGames.clear()
                }
                .subscribe(
                    { adGame ->
                        updateLocalMap(adGame)
                        val list = advertisedGames.values.toList()
                        advertisedGamesRelay.accept(list)
                    },
                    { error -> Logger.error(error) { "Error listening for advertised games" } }
                )
        )
    }

    fun stopListeningForAdvertisedGames() {
        Logger.debug { "Stop listening for advertised games" }
        disposableManager.disposeAndReset()
    }

    fun observeAdvertisedGames(): Observable<List<AdvertisedGame>> {
        Logger.debug { "Observing advertised games..." }
        return advertisedGamesRelay
    }

    fun getGame(ipAddress: String): AdvertisedGame? {
        return advertisedGames[IpAddress(ipAddress)]
    }

    private fun resumableGames(): Observable<List<GameCommonId>> {
        return store.observeGameCommonIdOfResumableGames()
            .subscribeOn(schedulerFactory.io())
    }

    private fun advertisedGames(): Observable<AdvertisedGame> {
        return gameDiscovery.listenForAdvertisedGames()
            .doOnNext { game -> idlingResource.decrement("Advertised game received ($game)") }
            .subscribeOn(schedulerFactory.io())
    }

    private fun staleAdvertisementsCleaner(): Observable<Long> {
        return Observable.interval(0L, GAME_AD_TIMEOUT_SEC.toLong(), TimeUnit.SECONDS, schedulerFactory.timeScheduler())
            .subscribeOn(schedulerFactory.io())
    }

    private fun updateLocalMap(advertisedGame: AdvertisedGame) {
        // Add new ad
        advertisedGames[IpAddress(advertisedGame.gameIpAddress)] = advertisedGame

        // Remove obsolete ads
        val timeNow = timeProvider.now()
        advertisedGames.filterValues { game -> advertisementIsTooOld(timeNow, game) }
            .keys
            .forEach { ip -> advertisedGames.remove(ip) }
        Logger.info { "advertisedGames: $advertisedGames" }
    }

    private fun advertisementIsTooOld(timeNow: LocalDateTime, game: AdvertisedGame): Boolean {
        return timeNow.minusSeconds(GAME_AD_TIMEOUT_SEC) > game.discoveryTime
    }

    private data class IpAddress(val value: String)
}
