package ch.qscqlmpa.dwitch.ui.home.joinnewgame

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitch.ui.model.UiControlModel
import ch.qscqlmpa.dwitchcommonutil.DisposableManager
import ch.qscqlmpa.dwitchcommonutil.scheduler.SchedulerFactory
import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitchgame.home.HomeGuestFacade
import timber.log.Timber
import javax.inject.Inject

class JoinNewGameViewModel @Inject constructor(
    private val guestFacade: HomeGuestFacade,
    disposableManager: DisposableManager,
    schedulerFactory: SchedulerFactory
) : BaseViewModel(disposableManager, schedulerFactory) {

    private val joinGameControlState = MutableLiveData(UiControlModel(enabled = false))
    private val command = MutableLiveData<JoinNewGameCommand>()

    private var playerName = ""

    fun observeJoinGameControlState(): LiveData<UiControlModel> {
        return joinGameControlState
    }

    fun observeCommands(): LiveData<JoinNewGameCommand> {
        return command
    }

    fun onPlayerNameChange(value: String) {
        playerName = value
        joinGameControlState.value = UiControlModel(enabled = playerName.isNotBlank())
    }

    fun joinGame(advertisedGame: AdvertisedGame) {
        require(playerName.isNotBlank()) { "Player name cannot be blank" }
        disposableManager.add(
            guestFacade.joinGame(advertisedGame, playerName)
                .observeOn(schedulerFactory.ui())
                .subscribe(
                    { command.setValue(JoinNewGameCommand.NavigateToWaitingRoom) },
                    { error -> Timber.e(error, "Error while joining the game") }
                )
        )
    }
}
