package ch.qscqlmpa.dwitch.ui.home.joinnewgame

import androidx.compose.runtime.State
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.mutableStateOf
import ch.qscqlmpa.dwitch.BuildConfig
import ch.qscqlmpa.dwitch.ui.Destination
import ch.qscqlmpa.dwitch.ui.NavigationBridge
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitchcommonutil.DwitchIdlingResource
import ch.qscqlmpa.dwitchgame.home.HomeGuestFacade
import io.reactivex.rxjava3.core.Scheduler
import org.tinylog.kotlin.Logger
import javax.inject.Inject

class JoinNewGameViewModel @Inject constructor(
    private val guestFacade: HomeGuestFacade,
    private val navigationBridge: NavigationBridge,
    private val uiScheduler: Scheduler,
    private val idlingResource: DwitchIdlingResource
) : BaseViewModel() {

    private val _loading = mutableStateOf(false)
    private val _joinGameControlEnabled = mutableStateOf(false)
    private val _playerName = mutableStateOf("")
    private val _notification = mutableStateOf<JoinNewGameNotification>(JoinNewGameNotification.None)

    val loading get(): State<Boolean> = _loading
    val playerName get(): State<String> = _playerName
    val canJoinGame get(): State<Boolean> = _joinGameControlEnabled
    val notification: State<JoinNewGameNotification> = _notification

    init {
        if (BuildConfig.DEBUG) {
            _joinGameControlEnabled.value = true
            _playerName.value = "Mébène"
        }
        Logger.debug { "Viewmodel lifecycle event: create JoinNewGameViewModel ($this)" }
    }

    fun gameName(ipAddress: String): State<String> {
        val game = guestFacade.getAdvertisedGame(ipAddress)
        if (game == null) _notification.value = JoinNewGameNotification.GameNotFound
        return derivedStateOf { game?.gameName ?: "" }
    }

    fun onPlayerNameChange(value: String) {
        _playerName.value = value
        _joinGameControlEnabled.value = playerName.value.isNotBlank()
    }

    fun onGameNotFoundAcknowledge() {
        navigationBridge.navigate(Destination.HomeScreens.Home)
    }

    fun joinGame(ipAddress: String) {
        idlingResource.increment("Joining game: wait for Dagger InGame component to be created")
        val game = guestFacade.getAdvertisedGame(ipAddress)
        if (game == null) {
            _notification.value = JoinNewGameNotification.GameNotFound
            return
        }

        val playerName = playerName.value
        require(playerName.isNotBlank()) { "Player name cannot be blank" }
        _loading.value = true
        disposableManager.add(
            guestFacade.joinGame(game, playerName)
                .observeOn(uiScheduler)
                .doOnTerminate { _loading.value = false }
                .subscribe(
                    { navigationBridge.navigate(Destination.HomeScreens.InGame) },
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
    object GameNotFound : JoinNewGameNotification()
    object ErrorJoiningGame : JoinNewGameNotification()
}
