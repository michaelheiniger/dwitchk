package ch.qscqlmpa.dwitch.ui.ingame.gameroom.host

import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitch.ui.navigation.HomeDestination
import ch.qscqlmpa.dwitch.ui.navigation.NavigationBridge
import ch.qscqlmpa.dwitch.ui.navigation.NavigationData
import ch.qscqlmpa.dwitchgame.gamediscovery.GameDiscoveryFacade
import ch.qscqlmpa.dwitchgame.ingame.InGameHostFacade
import ch.qscqlmpa.dwitchgame.ingame.gameroom.GameAction
import ch.qscqlmpa.dwitchgame.ingame.gameroom.PlayerFacade
import io.reactivex.rxjava3.core.Scheduler
import org.tinylog.kotlin.Logger
import javax.inject.Inject

internal class GameRoomHostViewModel @Inject constructor(
    private val facadeIn: InGameHostFacade,
    private val dashboardFacade: PlayerFacade,
    private val gameDiscoveryFacade: GameDiscoveryFacade,
    private val navigationBridge: NavigationBridge,
    private val uiScheduler: Scheduler
) : BaseViewModel() {

    fun startNewRound() {
        disposableManager.add(
            dashboardFacade.performAction(GameAction.StartNewRound)
                .observeOn(uiScheduler)
                .subscribe(
                    { Logger.debug { "Start new round successfully." } },
                    { error -> Logger.error(error) { "Error while starting new round." } }
                )
        )
    }

    fun endGame() {
        disposableManager.add(
            facadeIn.endGame()
                .observeOn(uiScheduler)
                .subscribe(
                    {
                        Logger.debug { "Game ended successfully." }
                        navigationBridge.navigate(HomeDestination.Home)
                    },
                    { error -> Logger.error(error) { "Error while ending game." } }
                )
        )
    }

    override fun onStart() {
        super.onStart()
        gameDiscoveryFacade.stopListeningForAdvertisedGames()
    }
}
