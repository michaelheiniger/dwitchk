package ch.qscqlmpa.dwitch.ui.ingame.gameroom

import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitch.ui.navigation.GameScreens
import ch.qscqlmpa.dwitch.ui.navigation.NavigationBridge
import ch.qscqlmpa.dwitchgame.ingame.GameFacadeToRename
import ch.qscqlmpa.dwitchmodel.game.RoomType
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import io.reactivex.rxjava3.core.Scheduler
import org.tinylog.Logger
import javax.inject.Inject

class GameViewModel @Inject constructor(
    private val gameFacadeToRename: GameFacadeToRename,
    private val navigationBridge: NavigationBridge,
    private val uiScheduler: Scheduler
) : BaseViewModel() {

    override fun onStart() {
        super.onStart()
        disposableManager.add(
            gameFacadeToRename.observeCurrentRoom()
                .take(1)
                .observeOn(uiScheduler)
                .map(::determineDestination)
                .subscribe(
                    { destination -> navigationBridge.navigate(destination) },
                    { error -> Logger.error(error) { "Error while fetching current room" } }
                )
        )
    }

    private fun determineDestination(currentRoom: RoomType) =
        when (currentRoom) {
            RoomType.WAITING_ROOM -> when (gameFacadeToRename.localPlayerRole) {
                PlayerRole.GUEST -> GameScreens.WaitingRoomGuest
                PlayerRole.HOST -> GameScreens.WaitingRoomHost
            }
            RoomType.GAME_ROOM -> when (gameFacadeToRename.localPlayerRole) {
                PlayerRole.GUEST -> GameScreens.GameRoomGuest
                PlayerRole.HOST -> GameScreens.GameRoomHost
            }
        }
}
