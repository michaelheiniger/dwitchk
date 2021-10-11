package ch.qscqlmpa.dwitch.ui.home.joinnewgame

import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import ch.qscqlmpa.dwitch.BuildConfig
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitch.ui.navigation.HomeScreens
import ch.qscqlmpa.dwitch.ui.navigation.NavigationBridge
import ch.qscqlmpa.dwitchcommonutil.DwitchIdlingResource
import ch.qscqlmpa.dwitchcommunication.GameAdvertisingInfo
import ch.qscqlmpa.dwitchgame.game.GameFacade
import io.reactivex.rxjava3.core.Scheduler
import org.tinylog.kotlin.Logger
import javax.inject.Inject

class JoinNewGameViewModel @Inject constructor(
    private val gameFacade: GameFacade,
    private val navigationBridge: NavigationBridge,
    private val uiScheduler: Scheduler,
    private val idlingResource: DwitchIdlingResource
) : BaseViewModel() {

    private val _loading = mutableStateOf(false)
    private val _joinGameControlEnabled = mutableStateOf(false)
    private val _playerName = mutableStateOf("")
    private val _game = mutableStateOf<GameAdvertisingInfo?>(null)
    private val _notification = mutableStateOf<JoinNewGameNotification>(JoinNewGameNotification.None)

    val loading get(): State<Boolean> = _loading
    val playerName get(): State<String> = _playerName
    val gameName get(): State<String> = derivedStateOf { _game.value?.gameName ?: "" }
    val canJoinGame get(): State<Boolean> = _joinGameControlEnabled
    val notification: State<JoinNewGameNotification> = _notification

    init {
        if (BuildConfig.DEBUG) {
            _joinGameControlEnabled.value = true
            _playerName.value = "Mébène"
        }
        Logger.debug { "Viewmodel lifecycle event: create JoinNewGameViewModel ($this)" }
    }

    fun loadGame(gameAd: GameAdvertisingInfo) {
        _game.value = gameAd
    }

    fun onPlayerNameChange(value: String) {
        _playerName.value = value
        _joinGameControlEnabled.value = playerName.value.isNotBlank()
    }

    fun joinGame() {
        idlingResource.increment("Joining game: wait for Dagger InGame component to be created")

        val playerName = playerName.value
        require(playerName.isNotBlank()) { "Player name cannot be blank" }
        _loading.value = true
        disposableManager.add(
            gameFacade.joinGame(_game.value!!, playerName)
                .observeOn(uiScheduler)
                .doOnTerminate { _loading.value = false }
                .subscribe(
                    { navigationBridge.navigate(HomeScreens.InGame) },
                    { error ->
                        _notification.value = JoinNewGameNotification.ErrorJoiningGame
                        Logger.error(error) { "Error while joining the game" }
                    }
                )
        )
    }

    override fun onCleared() {
        Logger.debug { "Viewmodel lifecycle event: clear JoinNewGameViewModel ($this)" }
        super.onCleared()
    }

    fun onBackClick() {
        navigationBridge.navigateBack()
    }
}

sealed class JoinNewGameNotification {
    object None : JoinNewGameNotification()
    object ErrorJoiningGame : JoinNewGameNotification()
}
