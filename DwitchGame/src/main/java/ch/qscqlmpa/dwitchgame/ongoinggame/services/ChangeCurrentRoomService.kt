package ch.qscqlmpa.dwitchgame.ongoinggame.services

import ch.qscqlmpa.dwitchgame.appevent.AppEvent
import ch.qscqlmpa.dwitchgame.appevent.AppEventRepository
import ch.qscqlmpa.dwitchmodel.game.RoomType
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import javax.inject.Inject

internal class ChangeCurrentRoomService @Inject constructor(
    private val store: InGameStore,
    private val appEventRepository: AppEventRepository,
) {

    fun moveToGameRoom() {
        store.updateGameRoom(RoomType.GAME_ROOM)
        appEventRepository.notify(AppEvent.GameRoomJoinedByHost)
    }
}
