package ch.qscqlmpa.dwitch.ui.ingame.gameroom

import ch.qscqlmpa.dwitch.ui.Destination
import ch.qscqlmpa.dwitch.ui.NavigationBridge
import ch.qscqlmpa.dwitch.ui.base.BaseViewModel
import ch.qscqlmpa.dwitchgame.ingame.GameFacade
import ch.qscqlmpa.dwitchmodel.game.RoomType
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import io.reactivex.rxjava3.core.Scheduler
import org.tinylog.Logger
import javax.inject.Inject

class GameViewModel @Inject constructor(
    private val gameFacade: GameFacade,
    private val navigationBridge: NavigationBridge,
    private val uiScheduler: Scheduler
) : BaseViewModel() {

    override fun onStart() {
        super.onStart()
        disposableManager.add(
            gameFacade.observeCurrentRoom()
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
            RoomType.WAITING_ROOM -> when (gameFacade.localPlayerRole) {
                PlayerRole.GUEST -> Destination.GameScreens.WaitingRoomGuest
                PlayerRole.HOST -> Destination.GameScreens.WaitingRoomHost
            }
            RoomType.GAME_ROOM -> when (gameFacade.localPlayerRole) {
                PlayerRole.GUEST -> Destination.GameScreens.GameRoomGuest
                PlayerRole.HOST -> Destination.GameScreens.GameRoomHost
            }
        }
}
