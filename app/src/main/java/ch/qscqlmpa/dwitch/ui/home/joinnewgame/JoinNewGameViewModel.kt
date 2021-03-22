package ch.qscqlmpa.dwitch.ui.home.joinnewgame

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitch.ui.model.UiControlModel
import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitchgame.home.HomeGuestFacade
import io.reactivex.rxjava3.core.Scheduler
import org.tinylog.kotlin.Logger
import javax.inject.Inject

class JoinNewGameViewModel @Inject constructor(
    private val guestFacade: HomeGuestFacade,
    private val uiScheduler: Scheduler
) : BaseViewModel() {

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
                .observeOn(uiScheduler)
                .subscribe(
                    { command.setValue(JoinNewGameCommand.NavigateToWaitingRoom) },
                    { error -> Logger.error(error) { "Error while joining the game" } }
                )
        )
    }
}
