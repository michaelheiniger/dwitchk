package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.host

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitchgame.ongoinggame.usecases.GameLaunchableEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom.WaitingRoomHostFacade
import io.reactivex.rxjava3.core.Scheduler
import org.tinylog.kotlin.Logger
import javax.inject.Inject

internal class WaitingRoomHostViewModel @Inject constructor(
    private val facade: WaitingRoomHostFacade,
    private val uiScheduler: Scheduler
) : BaseViewModel() {

    private val _commands = MutableLiveData<WaitingRoomHostCommand>()
    private val _canGameBeLaunched = MutableLiveData(false)

    val commands get(): LiveData<WaitingRoomHostCommand> = _commands
    val canGameBeLaunched get(): LiveData<Boolean> = _canGameBeLaunched

    override fun onStart() {
        super.onStart()
        canGameBeLaunched()
    }

    override fun onStop() {
        super.onStop()
        disposableManager.disposeAndReset()
    }

    fun launchGame() {
        disposableManager.add(
            facade.launchGame()
                .observeOn(uiScheduler)
                .subscribe(
                    {
                        Logger.info { "Game launched" }
                        _commands.value = WaitingRoomHostCommand.NavigateToGameRoomScreen
                    },
                    { error -> Logger.error(error) { "Error while launching game" } }
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
                        _commands.value = WaitingRoomHostCommand.NavigateToHomeScreen
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
