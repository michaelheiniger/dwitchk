package ch.qscqlmpa.dwitch.ui.home.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.app.AppEvent
import ch.qscqlmpa.dwitch.app.AppEventRepository
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitch.ui.common.LoadedData
import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.GuestGameLifecycleEvent
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.HostGameLifecycleEvent
import ch.qscqlmpa.dwitchgame.home.HomeFacade
import ch.qscqlmpa.dwitchgame.home.HomeGuestFacade
import ch.qscqlmpa.dwitchgame.home.HomeHostFacade
import ch.qscqlmpa.dwitchstore.model.ResumableGameInfo
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler
import org.tinylog.kotlin.Logger
import javax.inject.Inject

class MainActivityViewModel @Inject constructor(
    private val appEventRepository: AppEventRepository,
    homeFacade: HomeFacade,
    private val homeGuestFacade: HomeGuestFacade,
    private val homeHostFacade: HomeHostFacade,
    private val uiScheduler: Scheduler
) : BaseViewModel() {

    private val _loading = MutableLiveData<Boolean>()
    private val _commands = MutableLiveData<MainActivityCommands>()
    private val _advertisedGames = MutableLiveData<LoadedData<List<AdvertisedGame>>>()
    private val _resumableGames = MutableLiveData<LoadedData<List<ResumableGameInfo>>>()

    val loading get(): LiveData<Boolean> = _loading
    val advertisedGames get(): LiveData<LoadedData<List<AdvertisedGame>>> = _advertisedGames
    val resumableGames get(): LiveData<LoadedData<List<ResumableGameInfo>>> = _resumableGames
    val navigation get(): LiveData<MainActivityCommands> = _commands

    init {
        when (homeFacade.lastHostGameEvent()) {
            is HostGameLifecycleEvent.GameCreated -> MainActivityCommands.NavigateToWaitingRoomAsHost
            HostGameLifecycleEvent.MovedToGameRoom -> MainActivityCommands.NavigateToGameRoomAsHost
            else -> null // Nothing to do
        }?.also { _commands.value = it }

        when (homeFacade.lastGuestGameEvent()) {
            is GuestGameLifecycleEvent.GameJoined -> MainActivityCommands.NavigateToWaitingRoomAsGuest
            GuestGameLifecycleEvent.MovedToGameRoom -> MainActivityCommands.NavigateToGameRoomAsGuest
            else -> null // Nothing to do
        }?.also { _commands.value = it }
    }

    override fun onStart() {
        listenForAdvertisedGames()
        observeResumableGames()
    }

    fun joinGame(game: AdvertisedGame) {
        if (game.isNew) {
            _commands.value = MainActivityCommands.NavigateToNewGameActivityAsGuest(game)
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
                            _commands.value = MainActivityCommands.NavigateToWaitingRoomAsGuest
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
                    homeHostFacade.resumeGame(resumableGameInfo.id, 8889) // TODO: Take from sharedpref ?
                        .observeOn(uiScheduler)
                )
            )
                .doOnTerminate { _loading.value = false }
                .subscribe(
                    {
                        Logger.info { "Game resumed successfully." }
                        _commands.value = MainActivityCommands.NavigateToWaitingRoomAsHost
                    },
                    { error -> Logger.error(error) { "Error while resuming game." } }
                )
        )
    }

    private fun listenForAdvertisedGames() {
        disposableManager.add(
            homeGuestFacade.listenForAdvertisedGames()
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
