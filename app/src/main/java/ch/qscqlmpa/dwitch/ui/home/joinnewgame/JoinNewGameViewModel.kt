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

    private val _command = MutableLiveData<JoinNewGameCommand>()
    private val _joinGameControl = MutableLiveData(UiControlModel(enabled = false))
    private val _playerName = MutableLiveData("")

    val commands get(): LiveData<JoinNewGameCommand> = _command
    val playerName get(): LiveData<String> = _playerName
    val joinGameControl get(): LiveData<UiControlModel> = _joinGameControl

    fun onPlayerNameChange(value: String) {
        _playerName.value = value
        _joinGameControl.value = UiControlModel(enabled = !playerName.value.isNullOrBlank())
    }

    fun joinGame(advertisedGame: AdvertisedGame) {
        val playerName = playerName.value
        require(!playerName.isNullOrBlank()) { "Player name cannot be blank" }
        disposableManager.add(
            guestFacade.joinGame(advertisedGame, playerName)
                .observeOn(uiScheduler)
                .subscribe(
                    { _command.setValue(JoinNewGameCommand.NavigateToWaitingRoom) },
                    { error -> Logger.error(error) { "Error while joining the game" } }
                )
        )
    }
}
