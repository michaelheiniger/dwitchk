package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitch.ui.ongoinggame.GameScreens
import ch.qscqlmpa.dwitchgame.ongoinggame.GameFacade
import ch.qscqlmpa.dwitchmodel.game.RoomType
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import io.reactivex.rxjava3.core.Scheduler
import org.tinylog.Logger
import javax.inject.Inject


class GameViewModel @Inject constructor(
    private val gameFacade: GameFacade,
    uiScheduler: Scheduler
) : BaseViewModel() {

    private val _startScreen = MutableLiveData<GameScreens>()
    val startScreen get(): LiveData<GameScreens> = _startScreen

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
