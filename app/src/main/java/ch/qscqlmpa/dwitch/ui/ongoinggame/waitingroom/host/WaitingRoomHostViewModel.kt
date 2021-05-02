package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.host

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitchgame.ongoinggame.usecases.GameLaunchableEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom.PlayerWrUi
import ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom.WaitingRoomHostFacade
import io.reactivex.rxjava3.core.Scheduler
import org.tinylog.kotlin.Logger
import javax.inject.Inject

internal class WaitingRoomHostViewModel @Inject constructor(
    private val facade: WaitingRoomHostFacade,
    private val uiScheduler: Scheduler
) : BaseViewModel() {

    private val _navigation = MutableLiveData<WaitingRoomHostDestination>()
    private val _loading = MutableLiveData<Boolean>(false)
    private val _canGameBeLaunched = MutableLiveData(false)

    val navigation get(): LiveData<WaitingRoomHostDestination> = _navigation
    val loading get(): LiveData<Boolean> = _loading
    val canGameBeLaunched get(): LiveData<Boolean> = _canGameBeLaunched

    override fun onStart() {
        super.onStart()
        canGameBeLaunched()
    }

    fun addComputerPlayer() {
        disposableManager.add(
            facade.addComputerPlayer()
                .observeOn(uiScheduler)
                .subscribe(
                    {},
                    { error -> Logger.error(error) { "Error while adding a computer player." } }
                )
        )
    }

    fun kickPlayer(player: PlayerWrUi) {
        disposableManager.add(
            facade.kickPlayer(player)
                .observeOn(uiScheduler)
                .subscribe(
                    {},
                    { error -> Logger.error(error) { "Error while kicking player $player." } }
                )
        )
    }

    fun launchGame() {
        _loading.value = true
        disposableManager.add(
            facade.launchGame()
                .observeOn(uiScheduler)
                .doOnTerminate { _loading.value = false }
                .subscribe(
                    {
                        Logger.info { "Game launched" }
                        _navigation.value = WaitingRoomHostDestination.NavigateToGameRoomScreen
                    },
                    { error -> Logger.error(error) { "Error while launching game" } },
                )
        )
    }

    fun cancelGame() {
        disposableManager.add(
            facade.cancelGame()
                .observeOn(uiScheduler)
                .subscribe(
                    {
                        Logger.info { "Game canceled" }
                        _navigation.value = WaitingRoomHostDestination.NavigateToHomeScreen
                    },
                    { error -> Logger.error(error) { "Error while canceling game" } }
                )
        )
    }

    private fun canGameBeLaunched() {
        disposableManager.add(
            facade.observeGameLaunchableEvents()
                .observeOn(uiScheduler)
                .map(::processGameLaunchableEvent)
                .doOnError { error -> Logger.error(error) { "Error while observing if game can be launched." } }
                .subscribe { value -> _canGameBeLaunched.value = value }
        )
    }

    private fun processGameLaunchableEvent(event: GameLaunchableEvent): Boolean {
        return when (event) {
            GameLaunchableEvent.GameIsReadyToBeLaunched -> true
            GameLaunchableEvent.NotEnoughPlayers,
            GameLaunchableEvent.NotAllPlayersAreReady -> false
        }
    }
}
