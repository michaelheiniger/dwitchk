package ch.qscqlmpa.dwitch.ui.ingame.gameroom.host

import ch.qscqlmpa.dwitch.ui.Destination
import ch.qscqlmpa.dwitch.ui.NavigationBridge
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitchgame.common.GameAdvertisingFacade
import ch.qscqlmpa.dwitchgame.ingame.gameroom.GameAction
import ch.qscqlmpa.dwitchgame.ingame.gameroom.GameRoomHostFacade
import ch.qscqlmpa.dwitchgame.ingame.gameroom.PlayerFacade
import io.reactivex.rxjava3.core.Scheduler
import org.tinylog.kotlin.Logger
import javax.inject.Inject

internal class GameRoomHostViewModel @Inject constructor(
    private val facade: GameRoomHostFacade,
    private val dashboardFacade: PlayerFacade,
    private val gameAdvertisingFacade: GameAdvertisingFacade,
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
            facade.endGame()
                .observeOn(uiScheduler)
                .subscribe(
                    {
                        Logger.debug { "Game ended successfully." }
                        navigationBridge.navigate(Destination.HomeScreens.Home)
                    },
                    { error -> Logger.error(error) { "Error while ending game." } }
                )
        )
    }

    override fun onStart() {
        super.onStart()
        gameAdvertisingFacade.stopListeningForAdvertisedGames()
    }
}
