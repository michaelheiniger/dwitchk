package ch.qscqlmpa.dwitch.ui.ingame.gameroom.host

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitchgame.ingame.gameroom.GameAction
import ch.qscqlmpa.dwitchgame.ingame.gameroom.GameRoomHostFacade
import ch.qscqlmpa.dwitchgame.ingame.gameroom.PlayerFacade
import io.reactivex.rxjava3.core.Scheduler
import org.tinylog.kotlin.Logger
import javax.inject.Inject

internal class GameRoomHostViewModel @Inject constructor(
    private val facade: GameRoomHostFacade,
    private val dashboardFacade: PlayerFacade,
    private val uiScheduler: Scheduler
) : BaseViewModel() {

    private val _navigationCommand = mutableStateOf<GameRoomHostDestination>(GameRoomHostDestination.CurrentScreen)
    val navigation get(): State<GameRoomHostDestination> = _navigationCommand

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
                        _navigationCommand.value = GameRoomHostDestination.NavigateToHomeScreen
                    },
                    { error -> Logger.error(error) { "Error while ending game." } }
                )
        )
    }
}
