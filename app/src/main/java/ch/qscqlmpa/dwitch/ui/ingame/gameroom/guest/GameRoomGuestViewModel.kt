package ch.qscqlmpa.dwitch.ui.ingame.gameroom.guest

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitch.ui.navigation.HomeDestination
import ch.qscqlmpa.dwitch.ui.navigation.InGameGuestDestination
import ch.qscqlmpa.dwitch.ui.navigation.ScreenNavigator
import ch.qscqlmpa.dwitch.ui.navigation.navOptionsPopUpToInclusive
import ch.qscqlmpa.dwitchcommonutil.DwitchIdlingResource
import ch.qscqlmpa.dwitchgame.gamediscovery.GameDiscoveryFacade
import ch.qscqlmpa.dwitchgame.ingame.InGameGuestFacade
import ch.qscqlmpa.dwitchgame.ingame.gameevents.GuestGameEvent
import io.reactivex.rxjava3.core.Scheduler
import org.tinylog.kotlin.Logger
import javax.inject.Inject

class GameRoomGuestViewModel @Inject constructor(
    private val inGameGuestFacade: InGameGuestFacade,
    private val gameDiscoveryFacade: GameDiscoveryFacade,
    private val screenNavigator: ScreenNavigator,
    private val uiScheduler: Scheduler,
    private val idlingResource: DwitchIdlingResource
) : BaseViewModel() {

    private val _leavingGame = mutableStateOf(false)
    private val _gameOver = mutableStateOf(false)

    val gameOver get(): State<Boolean> = _gameOver
    val leavingGame get(): State<Boolean> = _leavingGame

    fun acknowledgeGameOver() {
        _gameOver.value = false
        goToHomeScreen()
    }

    fun leaveGame() {
        _leavingGame.value = true
        disposableManager.add(
            inGameGuestFacade.leaveGame()
                .observeOn(uiScheduler)
                .subscribe(
                    {
                        Logger.info { "Left game successfully" }
                        goToHomeScreen()
                    },
                    { error ->
                        Logger.error(error) { "Error while leaving the game." }
                        _leavingGame.value = false
                    }
                )
        )
    }

    override fun onStart() {
        super.onStart()
        gameDiscoveryFacade.startListeningForAdvertisedGames()
        observeGameEvent()
    }

    private fun goToHomeScreen() {
        screenNavigator.navigate(
            destination = HomeDestination.Home,
            navOptions = navOptionsPopUpToInclusive(InGameGuestDestination.GameRoom)
        )
    }

    private fun observeGameEvent() {
        disposableManager.add(
            inGameGuestFacade.observeGameEvents()
                .observeOn(uiScheduler)
                .doOnNext { event -> Logger.debug("Game event received: $event") }
                .filter { event -> event is GuestGameEvent.GameOver }
                .doOnNext { event -> idlingResource.decrement("Game event received ($event)") }
                .subscribe(
                    { _gameOver.value = true },
                    { error -> Logger.error(error) { "Error while observing game events." } }
                )
        )
    }
}
