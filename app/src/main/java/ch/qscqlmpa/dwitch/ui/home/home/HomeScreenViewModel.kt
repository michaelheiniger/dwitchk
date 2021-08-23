package ch.qscqlmpa.dwitch.ui.home.home

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import ch.qscqlmpa.dwitch.app.AppEvent
import ch.qscqlmpa.dwitch.app.AppEventRepository
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitch.ui.common.LoadedData
import ch.qscqlmpa.dwitchgame.common.GameAdvertisingFacade
import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitchgame.home.HomeFacade
import ch.qscqlmpa.dwitchgame.home.HomeGuestFacade
import ch.qscqlmpa.dwitchgame.home.HomeHostFacade
import ch.qscqlmpa.dwitchstore.model.ResumableGameInfo
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler
import org.tinylog.kotlin.Logger
import javax.inject.Inject

class HomeScreenViewModel @Inject constructor(
    private val appEventRepository: AppEventRepository,
    private val gameAdvertisingFacade: GameAdvertisingFacade,
    private val homeFacade: HomeFacade,
    private val homeGuestFacade: HomeGuestFacade,
    private val homeHostFacade: HomeHostFacade,
    private val uiScheduler: Scheduler
) : BaseViewModel() {

    private val _loading = mutableStateOf(false)
    private val _navigation = mutableStateOf<HomeDestination>(HomeDestination.CurrentScreen)
    private val _advertisedGames = mutableStateOf<LoadedData<List<AdvertisedGame>>>(LoadedData.Loading)
    private val _resumableGames = mutableStateOf<LoadedData<List<ResumableGameInfo>>>(LoadedData.Loading)

    val loading get(): State<Boolean> = _loading
    val advertisedGames get(): State<LoadedData<List<AdvertisedGame>>> = _advertisedGames
    val resumableGames get(): State<LoadedData<List<ResumableGameInfo>>> = _resumableGames
    val navigation get(): State<HomeDestination> = _navigation

    init {
        Logger.debug { "Viewmodel lifecycle event: create HomeScreenViewModel ($this)" }
        if (homeFacade.gameRunning) {
            _navigation.value = HomeDestination.GameFragment
        }
    }

    override fun onStart() {
        super.onStart()
        observeAdvertisedGames()
        observeResumableGames()
    }

    fun joinGame(game: AdvertisedGame) {
        if (game.isNew) {
            _navigation.value = HomeDestination.JoinNewGame(game)
        } else {
            _loading.value = true
            disposableManager.add(
                Completable.merge(
                    listOf(
                        appEventRepository.observeEvents()
                            .filter { event -> event is AppEvent.ServiceStarted }
                            .firstElement()
                            .ignoreElement(),
                        homeGuestFacade.joinResumedGame(game)
                            .observeOn(uiScheduler)
                    )
                )
                    .doOnTerminate { _loading.value = false }
                    .subscribe(
                        {
                            Logger.info { "Game resumed successfully." }
                            _navigation.value = HomeDestination.GameFragment
                        },
                        { error -> Logger.error(error) { "Error while resuming game." } }
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
                    homeHostFacade.resumeGame(resumableGameInfo.id)
                        .observeOn(uiScheduler)
                )
            )
                .doOnTerminate { _loading.value = false }
                .subscribe(
                    {
                        Logger.info { "Game resumed successfully." }
                        _navigation.value = HomeDestination.GameFragment
                    },
                    { error -> Logger.error(error) { "Error while resuming game." } }
                )
        )
    }

    override fun onCleared() {
        Logger.debug { "Viewmodel lifecycle event: clear HomeViewModel ($this)" }
        super.onCleared()
    }

    private fun observeAdvertisedGames() {
        disposableManager.add(
            gameAdvertisingFacade.observeAdvertisedGames()
                .observeOn(uiScheduler)
                .map<LoadedData<List<AdvertisedGame>>> { games -> LoadedData.Success(games) }
                .doOnError { error -> Logger.error(error) { "Error while observing advertised games." } }
                .onErrorReturn { LoadedData.Failed }
                .subscribe { response -> _advertisedGames.value = response }
        )
    }

    private fun observeResumableGames() {
        disposableManager.add(
            homeHostFacade.resumableGames()
                .observeOn(uiScheduler)
                .map<LoadedData<List<ResumableGameInfo>>> { games -> LoadedData.Success(games) }
                .doOnError { error -> Logger.error(error) { "Error while fetching existing games." } }
                .onErrorReturn { LoadedData.Failed }
                .subscribe { response -> _resumableGames.value = response }
        )
    }
}
