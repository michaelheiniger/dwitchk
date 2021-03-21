package ch.qscqlmpa.dwitch.ui.home.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitchgame.appevent.AppEvent
import ch.qscqlmpa.dwitchgame.appevent.AppEventRepository
import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitchgame.home.HomeGuestFacade
import ch.qscqlmpa.dwitchgame.home.HomeHostFacade
import ch.qscqlmpa.dwitchstore.model.ResumableGameInfo
import io.reactivex.rxjava3.core.Scheduler
import mu.KLogging
import javax.inject.Inject

class MainActivityViewModel @Inject
constructor(
    private val homeGuestFacade: HomeGuestFacade,
    private val homeHostFacade: HomeHostFacade,
    appEventRepository: AppEventRepository,
    private val uiScheduler: Scheduler
) : BaseViewModel() {

    private val _commands = MutableLiveData<MainActivityCommands>()
    private val _advertisedGames = MutableLiveData<AdvertisedGameResponse>()
    private val _resumableGames = MutableLiveData<ResumableGameResponse>()

    val advertisedGames get(): LiveData<AdvertisedGameResponse> = _advertisedGames
    val resumableGames get(): LiveData<ResumableGameResponse> = _resumableGames
    val commands get(): LiveData<MainActivityCommands> = _commands

    init {
        when (appEventRepository.lastEvent()) {
            is AppEvent.GameCreated -> MainActivityCommands.NavigateToGameRoomAsHost
            is AppEvent.GameJoined -> MainActivityCommands.NavigateToWaitingRoomAsGuest
            AppEvent.GameRoomJoinedByGuest -> MainActivityCommands.NavigateToGameRoomAsGuest
            AppEvent.GameRoomJoinedByHost -> MainActivityCommands.NavigateToGameRoomAsHost
            else -> null // Nothing to do
        }?.also { _commands.value = it }
    }

    override fun onStart() {
        listenForAdvertisedGames()
        observeExistingGames()
    }

    override fun onStop() {
        disposableManager.disposeAndReset()
    }

    fun joinGame(game: AdvertisedGame) {
        if (game.isNew) {
            _commands.value = MainActivityCommands.NavigateToNewGameActivityAsGuest(game)
        } else {
            disposableManager.add(
                homeGuestFacade.joinResumedGame(game)
                    .observeOn(uiScheduler)
                    .subscribe(
                        {
                            logger.info { "Game resumed successfully." }
                            _commands.value = MainActivityCommands.NavigateToWaitingRoomAsGuest
                        },
                        { error -> logger.error(error) { "Error while resuming game." } }
                    )
            )
        }
    }

    fun resumeGame(resumableGameInfo: ResumableGameInfo) {
        disposableManager.add(
            homeHostFacade.resumeGame(resumableGameInfo.id, 8889) // TODO: Take from sharedpref ?
                .observeOn(uiScheduler)
                .subscribe(
                    {
                        logger.info { "Game resumed successfully." }
                        _commands.value = MainActivityCommands.NavigateToWaitingRoomAsHost
                    },
                    { error -> logger.error(error) { "Error while resuming game." } }
                )
        )
    }

    private fun listenForAdvertisedGames() {
        disposableManager.add(
            homeGuestFacade.listenForAdvertisedGames()
                .observeOn(uiScheduler)
                .map { games -> AdvertisedGameResponse.success(games) }
                .onErrorReturn { error -> AdvertisedGameResponse.error(error) }
                .doOnError { error -> logger.error(error) { "Error while observing advertised games." } }
                .subscribe { response -> _advertisedGames.value = response }
        )

    }

    private fun observeExistingGames() {
        disposableManager.add(
            homeHostFacade.resumableGames()
                .observeOn(uiScheduler)
                .map { games -> ResumableGameResponse.success(games) }
                .onErrorReturn { error -> ResumableGameResponse.error(error) }
                .doOnError { error -> logger.error(error) { "Error while fetching existing games." } }
                .subscribe { response -> _resumableGames.value = response }
        )
    }

    companion object : KLogging()
}
