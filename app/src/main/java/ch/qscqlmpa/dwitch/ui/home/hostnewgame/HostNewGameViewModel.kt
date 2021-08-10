package ch.qscqlmpa.dwitch.ui.home.hostnewgame

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
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

    private val _navigation = mutableStateOf<HostNewGameDestination>(HostNewGameDestination.CurrentScreen)
    private val _loading = mutableStateOf(false)
    private val _hostGameControlEnabled = mutableStateOf(false)
    private val _playerName = mutableStateOf("")
    private val _gameName = mutableStateOf("")

    init {
        if (BuildConfig.DEBUG) {
            _hostGameControlEnabled.value = true
            _playerName.value = "Mirlick"
            _gameName.value = "Dwiiitch"
        }
        Logger.debug { "Viewmodel lifecycle event: create HostNewGameViewModel ($this)" }
    }

    val navigation get(): State<HostNewGameDestination> = _navigation
    val loading get(): State<Boolean> = _loading
    val playerName get(): State<String> = _playerName
    val gameName get(): State<String> = _gameName
    val hostGameControlEnabled get(): State<Boolean> = _hostGameControlEnabled

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
        require(playerName.isNotBlank()) { "Player name cannot be blank" }
        require(gameName.isNotBlank()) { "Game name cannot be blank" }
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
                    { _navigation.value = HostNewGameDestination.NavigateToWaitingRoom },
                    { error -> Logger.error(error) { "Error while start hosting the game" } }
                )
        )
    }

    override fun onCleared() {
        Logger.debug { "Viewmodel lifecycle event: clear HostNewGameViewModel ($this)" }
        super.onCleared()
    }

    private fun updateHostGameControl() {
        _hostGameControlEnabled.value = playerName.value.isNotBlank() && gameName.value.isNotBlank()
    }
}
