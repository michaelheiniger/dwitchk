package ch.qscqlmpa.dwitch.ui.ingame.waitingroom.host

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import ch.qscqlmpa.dwitch.ui.Destination
import ch.qscqlmpa.dwitch.ui.NavigationBridge
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitchcommonutil.DwitchIdlingResource
import ch.qscqlmpa.dwitchgame.gamediscovery.GameDiscoveryFacade
import ch.qscqlmpa.dwitchgame.ingame.usecases.GameLaunchableEvent
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.PlayerWrUi
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.WaitingRoomHostFacade
import io.reactivex.rxjava3.core.Scheduler
import org.tinylog.kotlin.Logger
import javax.inject.Inject

internal class WaitingRoomHostViewModel @Inject constructor(
    private val waitingRoomHostFacade: WaitingRoomHostFacade,
    private val gameDiscoveryFacade: GameDiscoveryFacade,
    private val navigationBridge: NavigationBridge,
    private val uiScheduler: Scheduler,
    private val idlingResource: DwitchIdlingResource
) : BaseViewModel() {

    private val _loading = mutableStateOf(false)
    private val _canGameBeLaunched = mutableStateOf(false)

    val loading get(): State<Boolean> = _loading
    val canGameBeLaunched get(): State<Boolean> = _canGameBeLaunched

    fun addComputerPlayer() {
        disposableManager.add(
            waitingRoomHostFacade.addComputerPlayer()
                .observeOn(uiScheduler)
                .subscribe(
                    {},
                    { error -> Logger.error(error) { "Error while adding a computer player." } }
                )
        )
    }

    fun kickPlayer(player: PlayerWrUi) {
        idlingResource.increment("Click to kick a player")
        disposableManager.add(
            waitingRoomHostFacade.kickPlayer(player)
                .observeOn(uiScheduler)
                .subscribe(
                    { Logger.info { "Player kicked successfully ($player)" } },
                    { error -> Logger.error(error) { "Error while kicking player $player." } }
                )
        )
    }

    fun launchGame() {
        _loading.value = true
        disposableManager.add(
            waitingRoomHostFacade.launchGame()
                .observeOn(uiScheduler)
                .doOnTerminate { _loading.value = false }
                .subscribe(
                    {
                        Logger.info { "Game launched" }
                        navigationBridge.navigate(Destination.GameScreens.GameRoomHost)
                    },
                    { error -> Logger.error(error) { "Error while launching game" } },
                )
        )
    }

    fun cancelGame() {
        disposableManager.add(
            waitingRoomHostFacade.cancelGame()
                .observeOn(uiScheduler)
                .subscribe(
                    {
                        Logger.info { "Game canceled" }
                        navigationBridge.navigate(Destination.HomeScreens.Home)
                    },
                    { error -> Logger.error(error) { "Error while canceling game" } }
                )
        )
    }

    override fun onStart() {
        super.onStart()
        gameDiscoveryFacade.stopListeningForAdvertisedGames()
        canGameBeLaunched()
    }

    private fun canGameBeLaunched() {
        disposableManager.add(
            waitingRoomHostFacade.observeGameLaunchableEvents()
                .observeOn(uiScheduler)
                .map(::isGameLaunchable)
                .subscribe(
                    { value -> _canGameBeLaunched.value = value },
                    { error -> Logger.error(error) { "Error while observing if game can be launched." } }
                )
        )
    }

    private fun isGameLaunchable(event: GameLaunchableEvent) = event.launchable
}
