package ch.qscqlmpa.dwitch.ui.home.hostnewgame

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.BuildConfig
import ch.qscqlmpa.dwitch.app.AppEvent
import ch.qscqlmpa.dwitch.app.AppEventRepository
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitchgame.home.HomeHostFacade
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler
import org.tinylog.kotlin.Logger
import javax.inject.Inject

class HostNewGameViewModel @Inject constructor(
    private val appEventRepository: AppEventRepository,
    private val hostFacade: HomeHostFacade,
    private val uiScheduler: Scheduler
) : BaseViewModel() {

    private val _navigationCommand = MutableLiveData<HostNewGameNavigationCommand>()
    private val _loading = MutableLiveData<Boolean>()
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

    val navigationCommand get(): LiveData<HostNewGameNavigationCommand> = _navigationCommand
    val loading get(): LiveData<Boolean> = _loading
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
        _loading.value = true
        disposableManager.add(
            Completable.merge(
                listOf(
                    appEventRepository.observeEvents()
                        .filter { event -> event is AppEvent.ServiceStarted }
                        .firstElement()
                        .ignoreElement(),
                    // TODO: Extract the port somewhere where it makes more sense
                    hostFacade.hostGame(gameName, playerName, 8889)
                        .observeOn(uiScheduler),
                )
            )
                .doOnTerminate { _loading.value = true }
                .subscribe(
                    { _navigationCommand.setValue(HostNewGameNavigationCommand.NavigateToWaitingRoom) },
                    { error -> Logger.error(error) { "Error while start hosting the game" } }
                )
        )
    }

    private fun updateHostGameControl() {
        _createGameControl.value = !playerName.value.isNullOrBlank() && !gameName.value.isNullOrBlank()
    }
}
