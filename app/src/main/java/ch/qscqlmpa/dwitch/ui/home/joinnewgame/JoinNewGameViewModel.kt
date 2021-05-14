package ch.qscqlmpa.dwitch.ui.home.joinnewgame

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.BuildConfig
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitchgame.home.HomeGuestFacade
import io.reactivex.rxjava3.core.Scheduler
import org.tinylog.kotlin.Logger
import javax.inject.Inject

class JoinNewGameViewModel @Inject constructor(
    private val guestFacade: HomeGuestFacade,
    private val uiScheduler: Scheduler
) : BaseViewModel() {

    private val _navigationCommand = MutableLiveData<JoinNewGameNavigationCommand>()
    private val _loading = MutableLiveData<Boolean>()
    private val _joinGameControl = MutableLiveData(false)
    private val _playerName = MutableLiveData("")

    init {
        if (BuildConfig.DEBUG) {
            _joinGameControl.value = true
            _playerName.value = "Mébène"
        }
    }

    val navigationCommand get(): LiveData<JoinNewGameNavigationCommand> = _navigationCommand
    val loading get(): LiveData<Boolean> = _loading
    val playerName get(): LiveData<String> = _playerName
    val joinGameControl get(): LiveData<Boolean> = _joinGameControl

    fun onPlayerNameChange(value: String) {
        _playerName.value = value
        _joinGameControl.value = !playerName.value.isNullOrBlank()
    }

    fun joinGame(advertisedGame: AdvertisedGame) {
        val playerName = playerName.value
        require(!playerName.isNullOrBlank()) { "Player name cannot be blank" }
        _loading.value = true
        disposableManager.add(
            guestFacade.joinGame(advertisedGame, playerName)
                .observeOn(uiScheduler)
                .doOnTerminate { _loading.value = true }
                .subscribe(
                    { _navigationCommand.setValue(JoinNewGameNavigationCommand.NavigateToWaitingRoom) },
                    { error -> Logger.error(error) { "Error while joining the game" } }
                )
        )
    }
}
