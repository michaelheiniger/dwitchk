package ch.qscqlmpa.dwitch.ui.home.hostnewgame

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.BuildConfig
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitchgame.home.HomeHostFacade
import io.reactivex.rxjava3.core.Scheduler
import org.tinylog.kotlin.Logger
import javax.inject.Inject

class HostNewGameViewModel @Inject constructor(
    private val hostFacade: HomeHostFacade,
    private val uiScheduler: Scheduler
) : BaseViewModel() {

    private val _command = MutableLiveData<HostNewGameCommand>()
    private val _createGameControl = MutableLiveData(false)
    private val _playerName = MutableLiveData("")
    private val _gameName = MutableLiveData("")

    init {
        if (BuildConfig.DEBUG) {
            _createGameControl.value = true
            _playerName.value = "Mirlick"
            _gameName.value = "Dwiiitch"
        }
    }

    val commands get(): LiveData<HostNewGameCommand> = _command
    val playerName get(): LiveData<String> = _playerName
    val gameName get(): LiveData<String> = _gameName
    val createGameControl get(): LiveData<Boolean> = _createGameControl

    fun onPlayerNameChange(value: String) {
        _playerName.value = value
        updateHostGameControl()
    }

    fun onGameNameChange(value: String) {
        _gameName.value = value
        updateHostGameControl()
    }

    fun hostGame() {
        val playerName = playerName.value
        val gameName = gameName.value
        require(!playerName.isNullOrBlank()) { "Player name cannot be blank" }
        require(!gameName.isNullOrBlank()) { "Game name cannot be blank" }
        disposableManager.add(
            hostFacade.hostGame(gameName, playerName, 8889) // TODO: Extract the port somewhere where it makes more sense
                .observeOn(uiScheduler)
                .subscribe(
                    { _command.setValue(HostNewGameCommand.NavigateToWaitingRoom) },
                    { error -> Logger.error(error) { "Error while start hosting the game" } }
                )
        )
    }

    private fun updateHostGameControl() {
        _createGameControl.value = !playerName.value.isNullOrBlank() && !gameName.value.isNullOrBlank()
    }
}
