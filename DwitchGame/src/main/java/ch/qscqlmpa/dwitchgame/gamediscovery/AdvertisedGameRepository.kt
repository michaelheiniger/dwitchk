package ch.qscqlmpa.dwitchgame.gamediscovery

import ch.qscqlmpa.dwitchcommonutil.DisposableManager
import ch.qscqlmpa.dwitchcommonutil.DwitchIdlingResource
import ch.qscqlmpa.dwitchcommonutil.TimeProvider
import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchcommunication.GameAdvertisingInfo
import ch.qscqlmpa.dwitchcommunication.gamediscovery.GameDiscovery
import ch.qscqlmpa.dwitchgame.di.GameScope
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchstore.store.Store
import com.jakewharton.rxrelay3.BehaviorRelay
import io.reactivex.rxjava3.core.Observable
import org.joda.time.LocalDateTime
import org.tinylog.kotlin.Logger
import java.util.concurrent.TimeUnit
import java.util.concurrent.atomic.AtomicBoolean
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
    private val advertisedGamesRelay = BehaviorRelay.createDefault<List<GameAdvertisingInfo>>(emptyList())
    private val advertisedGames = mutableMapOf<GameCommonId, GameAdvertisingInfo>() // Local cache surviving unsubscriptions
    private var listeningForAds = AtomicBoolean(false)

    /**
     * Listen for advertised games and store them in the repository until they become obsolete.
     * This method is idempotent.
     */
    fun startListeningForAdvertisedGames() {
        if (listeningForAds.get()) {
            return // for idempotency
        } else listeningForAds.set(true)

        Logger.debug { "Start listening for advertised games..." }
        disposableManager.add(
            Observable.combineLatest(
                resumableGames(),
                advertisedGames(),
                staleAdvertisementsCleaner()
            ) { existingGames, adGame, _ -> Pair(existingGames, adGame) }
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

    /**
     * Stop listen for advertised games and clears the store of game ads.
     * This method is idempotent.
     */
    fun stopListeningForAdvertisedGames() {
        if (!listeningForAds.get()) {
            return // for idempotency
        } else listeningForAds.set(false)

        Logger.debug { "Stop listening for advertised games" }
        disposableManager.disposeAndReset()
    }

    /**
     * Stream the list of advertised games in the repository.
     */
    fun observeAdvertisedGames(): Observable<List<GameAdvertisingInfo>> {
        Logger.debug { "Observing advertised games..." }
        return advertisedGamesRelay
    }

    fun getGame(gameCommonId: GameCommonId): GameAdvertisingInfo? {
        return advertisedGames[gameCommonId]
    }

    private fun resumableGames(): Observable<List<GameCommonId>> {
        return store.observeGameCommonIdOfResumableGames()
            .subscribeOn(schedulerFactory.io())
    }

    private fun advertisedGames(): Observable<GameAdvertisingInfo> {
        return gameDiscovery.listenForAdvertisedGames()
            .doOnNext { game -> idlingResource.decrement("Advertised game received ($game)") }
            .subscribeOn(schedulerFactory.io())
    }

    private fun staleAdvertisementsCleaner(): Observable<Long> {
        return Observable.interval(0L, GAME_AD_TIMEOUT_SEC.toLong(), TimeUnit.SECONDS, schedulerFactory.timeScheduler())
            .subscribeOn(schedulerFactory.io())
    }

    private fun updateLocalMap(advertisedGame: GameAdvertisingInfo) {
        // Add new ad
        advertisedGames[advertisedGame.gameCommonId] = advertisedGame

        // Remove obsolete ads
        val timeNow = timeProvider.now()
        advertisedGames.filterValues { game -> advertisementIsTooOld(timeNow, game) }
            .keys
            .forEach { gameCommonId -> advertisedGames.remove(gameCommonId) }
        Logger.trace { "advertisedGames: $advertisedGames" }
    }

    private fun advertisementIsTooOld(timeNow: LocalDateTime, game: GameAdvertisingInfo): Boolean {
        return timeNow.minusSeconds(GAME_AD_TIMEOUT_SEC) > game.discoveryTime
    }
}
