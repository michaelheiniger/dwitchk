package ch.qscqlmpa.dwitch.ui.home.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import ch.qscqlmpa.dwitch.app.AppEvent
import ch.qscqlmpa.dwitch.app.AppEventRepository
import ch.qscqlmpa.dwitch.ingame.services.ServiceManager
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitch.ui.common.LoadedData
import ch.qscqlmpa.dwitch.ui.navigation.*
import ch.qscqlmpa.dwitchcommunication.GameAdvertisingInfo
import ch.qscqlmpa.dwitchcommunication.deviceconnectivity.DeviceConnectionState
import ch.qscqlmpa.dwitchcommunication.deviceconnectivity.DeviceConnectivityRepository
import ch.qscqlmpa.dwitchgame.game.GameFacade
import ch.qscqlmpa.dwitchgame.gamediscovery.GameDiscoveryFacade
import ch.qscqlmpa.dwitchgame.gamelifecycle.GameLifecycleFacade
import ch.qscqlmpa.dwitchgame.gamelifecycle.GameLifecycleState
import ch.qscqlmpa.dwitchmodel.game.RoomType
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
    private val deviceConnectivityRepository: DeviceConnectivityRepository,
    private val screenNavigator: ScreenNavigator,
    private val uiScheduler: Scheduler
) : BaseViewModel() {

    private val _loading = mutableStateOf(false)
    private val _advertisedGames = mutableStateOf<LoadedData<List<GameAdvertisingInfo>>>(LoadedData.Loading)
    private val _resumableGames = mutableStateOf<LoadedData<List<ResumableGameInfo>>>(LoadedData.Loading)
    private val _notification = mutableStateOf<HomeNotification>(HomeNotification.None)

    // Don't want to show the banner before the actual value is observed
    // but disable the controls in the mean time
    private val _connectedToWlan = mutableStateOf(true)
    private val _controlsEnabled = mutableStateOf(false)

    val loading get(): State<Boolean> = _loading
    val advertisedGames get(): State<LoadedData<List<GameAdvertisingInfo>>> = _advertisedGames
    val resumableGames get(): State<LoadedData<List<ResumableGameInfo>>> = _resumableGames
    val notification: State<HomeNotification> = _notification
    val connectedToWlan: State<Boolean> = _connectedToWlan
    val controlsEnabled: State<Boolean> = _controlsEnabled

    init {
        Logger.debug { "Viewmodel lifecycle event: create HomeScreenViewModel ($this)" }
    }

    fun createNewGame() {
        screenNavigator.navigate(HomeDestination.HostNewGame)
    }

    fun joinGame(game: GameAdvertisingInfo) {
        if (game.isNew) {
            screenNavigator.navigate(HomeDestination.JoinNewGame(game))
        } else {
            joinExistingGame(game)
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
                        screenNavigator.navigate(
                            destination = InGameHostDestination.WaitingRoom,
                            navOptions = navOptionsPopUpToInclusive(HomeDestination.Home)
                        )
                    },
                    { error -> Logger.error(error) { "Error while resuming game." } }
                )
        )
    }

    fun deleteExistingGame(resumableGameInfo: ResumableGameInfo) {
        disposableManager.add(
            gameFacade.deleteExistingGame(resumableGameInfo.id)
                .subscribe(
                    { Logger.info { "Existing game deleted successfully." } },
                    { error -> Logger.error(error) { "Error while deleting existing game." } }
                )
        )
    }

    override fun onStart() {
        super.onStart()
        gameDiscoveryFacade.startListeningForAdvertisedGames()
        observeConnectionState()
        observeAdvertisedGames()
        observeResumableGames()
        reactToGameState()
    }

    override fun onCleared() {
        Logger.debug { "Viewmodel lifecycle event: clear HomeViewModel ($this)" }
        super.onCleared()
    }

    fun load(gameAd: GameAdvertisingInfo) {
        Logger.debug { "Load game ad: $gameAd" }
        if (gameAd.isNew) {
            screenNavigator.navigate(HomeDestination.JoinNewGame(gameAd))
        } else {
            joinExistingGame(gameAd)
        }
    }

    private fun joinExistingGame(game: GameAdvertisingInfo) {
        require(!game.isNew)
        _loading.value = true
        disposableManager.add(
            gameFacade.joinResumedGame(game)
                .observeOn(uiScheduler)
                .doOnTerminate { _loading.value = false }
                .subscribe(
                    { currentRoom ->
                        @Suppress("WHEN_ENUM_CAN_BE_NULL_IN_JAVA")
                        val destination = when (currentRoom) {
                            RoomType.WAITING_ROOM -> InGameGuestDestination.WaitingRoom
                            RoomType.GAME_ROOM -> InGameGuestDestination.GameRoom
                        }
                        Logger.info { "Game resumed successfully." }
                        screenNavigator.navigate(
                            destination = destination,
                            navOptions = navOptionsPopUpToInclusive(HomeDestination.Home)
                        )
                    },
                    { error ->
                        _notification.value = HomeNotification.ErrorJoiningGame
                        Logger.error(error) { "Error while joining resumed game." }
                    }
                )
        )
    }

    private fun reactToGameState() {
        when (gameLifecycleFacade.currentLifecycleState) {
            GameLifecycleState.NotStarted -> {
                // Nothing to do
            }
            GameLifecycleState.RunningWaitingRoomGuest -> navigateToInGame(InGameGuestDestination.WaitingRoom)
            GameLifecycleState.RunningWaitingRoomHost -> navigateToInGame(InGameHostDestination.WaitingRoom)
            GameLifecycleState.RunningGameRoomGuest -> navigateToInGame(InGameGuestDestination.GameRoom)
            GameLifecycleState.RunningGameRoomHost -> navigateToInGame(InGameHostDestination.GameRoom)
            GameLifecycleState.Over -> serviceManager.stop()
        }
    }

    private fun navigateToInGame(destination: Destination) {
        Logger.debug { "Game is running: navigate to $destination" }
        screenNavigator.navigate(
            destination = destination,
            navOptions = navOptionsPopUpToInclusive(HomeDestination.Home)
        )
    }

    private fun observeConnectionState() {
        disposableManager.add(
            deviceConnectivityRepository.observeConnectionState()
                .observeOn(uiScheduler)
                .retry()
                .subscribe(
                    { state ->
                        _controlsEnabled.value = state is DeviceConnectionState.ConnectedToWlan
                        _connectedToWlan.value = state is DeviceConnectionState.ConnectedToWlan
                    },
                    { error -> Logger.error(error) { "Error while observing the connection state of the device" } }
                )
        )
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
