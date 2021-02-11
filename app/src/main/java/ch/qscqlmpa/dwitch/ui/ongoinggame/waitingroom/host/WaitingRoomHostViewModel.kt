package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.host

import androidx.lifecycle.LiveData
import androidx.lifecycle.LiveDataReactiveStreams
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitch.ui.model.UiControlModel
import ch.qscqlmpa.dwitchgame.ongoinggame.usecases.GameLaunchableEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom.WaitingRoomHostFacade
import io.reactivex.rxjava3.core.BackpressureStrategy
import io.reactivex.rxjava3.core.Scheduler
import mu.KLogging
import javax.inject.Inject

internal class WaitingRoomHostViewModel @Inject constructor(
    private val facade: WaitingRoomHostFacade,
    private val uiScheduler: Scheduler
) : BaseViewModel() {

    private val commands = MutableLiveData<WaitingRoomHostCommand>()

    fun canGameBeLaunched(): LiveData<UiControlModel> {
        return LiveDataReactiveStreams.fromPublisher(
            facade.observeGameLaunchableEvents()
                .observeOn(uiScheduler)
                .map(::processGameLaunchableEvent)
                .doOnError { error -> logger.error(error) { "Error while observing if game can be launched." } }
                .toFlowable(BackpressureStrategy.LATEST)
        )
    }

    fun commands(): LiveData<WaitingRoomHostCommand> {
        return commands
    }

    fun launchGame() {
        disposableManager.add(
            facade.launchGame()
                .observeOn(uiScheduler)
                .subscribe(
                    {
                        logger.info { "Game launched" }
                        commands.value = WaitingRoomHostCommand.NavigateToGameRoomScreen
                    },
                    { error -> logger.error(error) { "Error while launching game" } }
                )
        )
    }

    fun cancelGame() {
        disposableManager.add(
            facade.cancelGame()
                .observeOn(uiScheduler)
                .subscribe(
                    {
                        logger.info { "Game canceled" }
                        commands.value = WaitingRoomHostCommand.NavigateToHomeScreen
                    },
                    { error -> logger.error(error) { "Error while canceling game" } }
                )
        )
    }

    private fun processGameLaunchableEvent(event: GameLaunchableEvent): UiControlModel {
        return when (event) {
            GameLaunchableEvent.GameIsReadyToBeLaunched -> UiControlModel(enabled = true)
            GameLaunchableEvent.NotEnoughPlayers -> UiControlModel(enabled = false)
            GameLaunchableEvent.NotAllPlayersAreReady -> UiControlModel(enabled = false)
        }
    }

    companion object : KLogging()
}
