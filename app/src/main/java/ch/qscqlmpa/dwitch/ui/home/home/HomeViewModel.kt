package ch.qscqlmpa.dwitch.ui.home.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import ch.qscqlmpa.dwitch.app.AppEvent
import ch.qscqlmpa.dwitch.app.AppEventRepository
import ch.qscqlmpa.dwitch.ingame.services.ServiceManager
import ch.qscqlmpa.dwitch.ui.Destination
import ch.qscqlmpa.dwitch.ui.NavigationBridge
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitch.ui.common.LoadedData
import ch.qscqlmpa.dwitchcommunication.GameAdvertisingInfo
import ch.qscqlmpa.dwitchgame.game.GameFacade
import ch.qscqlmpa.dwitchgame.gamediscovery.GameDiscoveryFacade
import ch.qscqlmpa.dwitchgame.gamelifecycle.GameLifecycleFacade
import ch.qscqlmpa.dwitchgame.gamelifecycle.GameLifecycleState
import ch.qscqlmpa.dwitchstore.model.ResumableGameInfo
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler
import org.tinylog.kotlin.Logger
import javax.inject.Inject

@Suppress("LongParameterList")
class HomeViewModel @Inject constructor(
    private val appEventRepository: AppEventRepository,
    private val serviceManager: ServiceManager,
    private val gameDiscoveryFacade: GameDiscoveryFacade,
    private val gameLifecycleFacade: GameLifecycleFacade,
    private val gameFacade: GameFacade,
    private val navigationBridge: NavigationBridge,
    private val uiScheduler: Scheduler
) : BaseViewModel() {

    private val _loading = mutableStateOf(false)
    private val _advertisedGames = mutableStateOf<LoadedData<List<GameAdvertisingInfo>>>(LoadedData.Loading)
    private val _resumableGames = mutableStateOf<LoadedData<List<ResumableGameInfo>>>(LoadedData.Loading)
    private val _notification = mutableStateOf<HomeNotification>(HomeNotification.None)

    val loading get(): State<Boolean> = _loading
    val advertisedGames get(): State<LoadedData<List<GameAdvertisingInfo>>> = _advertisedGames
    val resumableGames get(): State<LoadedData<List<ResumableGameInfo>>> = _resumableGames
    val notification: State<HomeNotification> = _notification

    init {
        Logger.debug { "Viewmodel lifecycle event: create HomeScreenViewModel ($this)" }
    }

    fun createNewGame() {
        navigationBridge.navigate(Destination.HomeScreens.HostNewGame)
    }

    fun joinGame(game: GameAdvertisingInfo) {
        if (game.isNew) {
            navigationBridge.navigate(Destination.HomeScreens.JoinNewGame(game.gameCommonId))
        } else {
            _loading.value = true
            disposableManager.add(
                gameFacade.joinResumedGame(game)
                    .observeOn(uiScheduler)
                    .doOnTerminate { _loading.value = false }
                    .subscribe(
                        {
                            Logger.info { "Game resumed successfully." }
                            navigationBridge.navigate(Destination.HomeScreens.InGame)
                        },
                        { error ->
                            _notification.value = HomeNotification.ErrorJoiningGame
                            Logger.error(error) { "Error while resuming game." }
                        }
                    )
            )
        }
    }

    fun resumeGame(resumableGameInfo: ResumableGameInfo) {
        _loading.value = true
        disposableManager.add(
            Completable.merge(
                listOf(
                    appEventRepository.observeEvents()
                        .filter { event -> event is AppEvent.ServiceStarted }
                        .firstElement()
                        .ignoreElement(),
                    gameFacade.resumeGame(resumableGameInfo.id)
                        .observeOn(uiScheduler)
                )
            )
                .doOnTerminate { _loading.value = false }
                .subscribe(
                    {
                        Logger.info { "Game resumed successfully." }
                        navigationBridge.navigate(Destination.HomeScreens.InGame)
                    },
                    { error -> Logger.error(error) { "Error while resuming game." } }
                )
        )
    }

    override fun onStart() {
        super.onStart()
        gameDiscoveryFacade.startListeningForAdvertisedGames()
        observeAdvertisedGames()
        observeResumableGames()
        reactToGameState()
    }

    override fun onCleared() {
        Logger.debug { "Viewmodel lifecycle event: clear HomeViewModel ($this)" }
        super.onCleared()
    }

    private fun reactToGameState() {
        when (gameLifecycleFacade.currentLifecycleState) {
            GameLifecycleState.NotStarted -> {
                // Nothing to do
            }
            GameLifecycleState.Running -> {
                Logger.debug { "Game is running: navigate to ${Destination.HomeScreens.InGame}" }
                navigationBridge.navigate(Destination.HomeScreens.InGame)
            }
            GameLifecycleState.Over -> serviceManager.stop()
        }
    }

    private fun observeAdvertisedGames() {
        disposableManager.add(
            gameDiscoveryFacade.observeAdvertisedGames()
                .observeOn(uiScheduler)
                .map<LoadedData<List<GameAdvertisingInfo>>> { games -> LoadedData.Success(games) }
                .doOnError { error -> Logger.error(error) { "Error while observing advertised games." } }
                .onErrorReturn { LoadedData.Failed }
                .subscribe { response -> _advertisedGames.value = response }
        )
    }

    private fun observeResumableGames() {
        disposableManager.add(
            gameFacade.resumableGames()
                .observeOn(uiScheduler)
                .map<LoadedData<List<ResumableGameInfo>>> { games -> LoadedData.Success(games) }
                .doOnError { error -> Logger.error(error) { "Error while fetching existing games." } }
                .onErrorReturn { LoadedData.Failed }
                .subscribe { response -> _resumableGames.value = response }
        )
    }
}

sealed class HomeNotification {
    object None : HomeNotification()
    object ErrorJoiningGame : HomeNotification()
}
