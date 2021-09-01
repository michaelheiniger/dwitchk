package ch.qscqlmpa.dwitch.ui.home.hostnewgame

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import ch.qscqlmpa.dwitch.BuildConfig
import ch.qscqlmpa.dwitch.app.AppEvent
import ch.qscqlmpa.dwitch.app.AppEventRepository
import ch.qscqlmpa.dwitch.ui.Destination
import ch.qscqlmpa.dwitch.ui.NavigationBridge
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitchgame.common.GameAdvertisingFacade
import ch.qscqlmpa.dwitchgame.home.HomeHostFacade
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Scheduler
import org.tinylog.kotlin.Logger
import javax.inject.Inject

class HostNewGameViewModel @Inject constructor(
    private val appEventRepository: AppEventRepository,
    private val hostFacade: HomeHostFacade,
    private val gameAdvertisingFacade: GameAdvertisingFacade,
    private val navigationBridge: NavigationBridge,
    private val uiScheduler: Scheduler
) : BaseViewModel() {

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

    val loading get(): State<Boolean> = _loading
    val playerName get(): State<String> = _playerName
    val gameName get(): State<String> = _gameName
    val canGameBeCreated get(): State<Boolean> = _hostGameControlEnabled

    fun onPlayerNameChange(value: String) {
        _playerName.value = value
        updateHostGameControl()
    }

    fun onGameNameChange(value: String) {
        _gameName.value = value
        updateHostGameControl()
    }

    fun onBackClick() {
        navigationBridge.navigateBack()
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
                    hostFacade.hostGame(gameName, playerName)
                        .observeOn(uiScheduler),
                )
            )
                .doOnTerminate { _loading.value = true }
                .subscribe(
                    { navigationBridge.navigate(Destination.HomeScreens.InGame) },
                    { error -> Logger.error(error) { "Error while start hosting the game" } }
                )
        )
    }

    override fun onStart() {
        super.onStart()
        gameAdvertisingFacade.stopListeningForAdvertisedGames()
    }

    override fun onCleared() {
        Logger.debug { "Viewmodel lifecycle event: clear HostNewGameViewModel ($this)" }
        super.onCleared()
    }

    private fun updateHostGameControl() {
        _hostGameControlEnabled.value = playerName.value.isNotBlank() && gameName.value.isNotBlank()
    }
}
