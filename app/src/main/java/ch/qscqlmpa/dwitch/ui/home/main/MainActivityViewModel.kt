package ch.qscqlmpa.dwitch.ui.home.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitchgame.home.HomeGuestFacade
import ch.qscqlmpa.dwitchgame.home.HomeHostFacade
import ch.qscqlmpa.dwitchmodel.game.ResumableGameInfo
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Scheduler
import timber.log.Timber
import javax.inject.Inject

class MainActivityViewModel @Inject
constructor(
    private val homeGuestFacade: HomeGuestFacade,
    private val homeHostFacade: HomeHostFacade,
    private val uiScheduler: Scheduler
) : BaseViewModel() {

    private val commands = MutableLiveData<MainActivityCommands>()

    fun commands(): LiveData<MainActivityCommands> {
        return commands
    }

    fun observeAdvertisedGames(): LiveData<AdvertisedGameResponse> {
        return LiveDataReactiveStreams.fromPublisher(
            homeGuestFacade.listenForAdvertisedGames()
                .observeOn(uiScheduler)
                .map { games -> AdvertisedGameResponse.success(games) }
                .onErrorReturn { error -> AdvertisedGameResponse.error(error) }
                .doOnError { error -> Timber.e(error, "Error while observing advertised games.") }
                .doFinally { homeGuestFacade.stopListeningForAdvertiseGames() }
                .toFlowable(BackpressureStrategy.LATEST)
        )
    }

    fun observeExistingGames(): LiveData<ExistingGameResponse> {
        return LiveDataReactiveStreams.fromPublisher(
            homeHostFacade.resumableGames()
                .observeOn(uiScheduler)
                .map { games -> ExistingGameResponse.success(games) }
                .onErrorReturn { error -> ExistingGameResponse.error(error) }
                .doOnError { error -> Timber.e(error, "Error while fetching existing games.") }
                .toFlowable(BackpressureStrategy.LATEST)
        )
    }

    fun joinGame(game: AdvertisedGame) {
        if (game.isNew) {
            commands.value = MainActivityCommands.NavigateToNewGameActivityAsGuest(game)
        } else {
            disposableManager.add(
                homeGuestFacade.joinResumedGame(game)
                    .observeOn(uiScheduler)
                    .subscribe(
                        {
                            Timber.i("Game resumed successfully.")
                            commands.value = MainActivityCommands.NavigateToWaitingRoomAsGuest
                        },
                        { error -> Timber.e(error, "Error while resuming game.") }
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
                        Timber.i("Game resumed successfully.")
                        commands.value = MainActivityCommands.NavigateToWaitingRoomAsHost
                    },
                    { error -> Timber.e(error, "Error while resuming game.") }
                )
        )
    }
}
