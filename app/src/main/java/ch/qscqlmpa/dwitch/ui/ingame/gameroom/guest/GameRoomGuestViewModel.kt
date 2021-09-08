package ch.qscqlmpa.dwitch.ui.ingame.gameroom.guest

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import ch.qscqlmpa.dwitch.ui.Destination
import ch.qscqlmpa.dwitch.ui.NavigationBridge
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitchcommonutil.DwitchIdlingResource
import ch.qscqlmpa.dwitchgame.gamediscovery.GameDiscoveryFacade
import ch.qscqlmpa.dwitchgame.ingame.InGameGuestFacade
import ch.qscqlmpa.dwitchgame.ingame.gameevents.GuestGameEvent
import io.reactivex.rxjava3.core.Scheduler
import org.tinylog.kotlin.Logger
import javax.inject.Inject

internal class GameRoomGuestViewModel @Inject constructor(
    private val inGameGuestFacade: InGameGuestFacade,
    private val gameDiscoveryFacade: GameDiscoveryFacade,
    private val navigationBridge: NavigationBridge,
    private val uiScheduler: Scheduler,
    private val idlingResource: DwitchIdlingResource
) : BaseViewModel() {

    private val _gameOver = mutableStateOf(false)
    val gameOver get(): State<Boolean> = _gameOver

    fun acknowledgeGameOver() {
        _gameOver.value = false
        goToHomeScreen()
    }

    fun leaveGame() {
        disposableManager.add(
            inGameGuestFacade.leaveGame()
                .observeOn(uiScheduler)
                .subscribe(
                    {
                        Logger.info { "Left game successfully" }
                        goToHomeScreen()
                    },
                    { error -> Logger.error(error) { "Error while leaving the game." } }
                )
        )
    }

    override fun onStart() {
        super.onStart()
        gameDiscoveryFacade.startListeningForAdvertisedGames()
        observeGameEvent()
    }

    private fun goToHomeScreen() {
        navigationBridge.navigate(Destination.HomeScreens.Home)
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
