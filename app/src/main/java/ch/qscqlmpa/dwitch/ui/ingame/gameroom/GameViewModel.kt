package ch.qscqlmpa.dwitch.ui.ingame.gameroom

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitch.ui.ingame.GameScreens
import ch.qscqlmpa.dwitchgame.ingame.GameFacade
import ch.qscqlmpa.dwitchmodel.game.RoomType
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import io.reactivex.rxjava3.core.Scheduler
import org.tinylog.Logger
import javax.inject.Inject


class GameViewModel @Inject constructor(
    private val gameFacade: GameFacade,
    uiScheduler: Scheduler
) : BaseViewModel() {

    private val _startScreen = mutableStateOf<GameScreens>(GameScreens.Loading)
    val startScreen get(): State<GameScreens> = _startScreen

    init {
        disposableManager.add(
            gameFacade.observeCurrentRoom()
                .observeOn(uiScheduler)
                .subscribe(
                    { room -> _startScreen.value = determineStartScreen(room) },
                    { error -> Logger.error(error) { "Error while fetching current room" } }
                )
        )
    }

    private fun determineStartScreen(currentRoom: RoomType) =
        when (currentRoom) {
            RoomType.WAITING_ROOM -> when (gameFacade.localPlayerRole) {
                PlayerRole.GUEST -> GameScreens.WaitingRoomGuest
                PlayerRole.HOST -> GameScreens.WaitingRoomHost
            }
            RoomType.GAME_ROOM -> when (gameFacade.localPlayerRole) {
                PlayerRole.GUEST -> GameScreens.GameRoomGuest
                PlayerRole.HOST -> GameScreens.GameRoomHost
            }
        }
}
