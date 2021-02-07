package ch.qscqlmpa.dwitch.ui.home.hostnewgame

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitch.ui.model.UiControlModel
import ch.qscqlmpa.dwitchgame.home.HomeHostFacade
import io.reactivex.rxjava3.core.Scheduler
import timber.log.Timber
import javax.inject.Inject

class HostNewGameViewModel @Inject constructor(
    private val hostFacade: HomeHostFacade,
    private val uiScheduler: Scheduler
) : BaseViewModel() {

    private val hostGameControlState = MutableLiveData(UiControlModel(enabled = false))
    private val command = MutableLiveData<HostNewGameCommand>()

    private var playerName = ""
    private var gameName = ""

    fun observeHostGameControleState(): LiveData<UiControlModel> {
        return hostGameControlState
    }

    fun observeCommands(): LiveData<HostNewGameCommand> {
        return command
    }

    fun onPlayerNameChange(value: String) {
        playerName = value
        updateHostGameControlState()
    }

    fun onGameNameChange(value: String) {
        gameName = value
        updateHostGameControlState()
    }

    fun hostGame() {
        require(playerName.isNotBlank()) { "Player name cannot be blank" }
        require(gameName.isNotBlank()) { "Game name cannot be blank" }
        disposableManager.add(
            hostFacade.hostGame(gameName, playerName, 8889) // TODO: Take value from sharedPref
                .observeOn(uiScheduler)
                .subscribe(
                    { command.setValue(HostNewGameCommand.NavigateToWaitingRoom) },
                    { error -> Timber.e(error, "Error while start hosting the game") }
                )
        )
    }

    private fun updateHostGameControlState() {
        hostGameControlState.value = UiControlModel(enabled = playerName.isNotBlank() && gameName.isNotBlank())
    }
}
