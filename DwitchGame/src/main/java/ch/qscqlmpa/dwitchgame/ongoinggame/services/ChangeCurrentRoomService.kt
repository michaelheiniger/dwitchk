package ch.qscqlmpa.dwitchgame.ongoinggame.services

import ch.qscqlmpa.dwitchgame.gamelifecycleevents.GuestGameLifecycleEvent
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.GuestGameLifecycleEventRepository
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.HostGameLifecycleEvent
import ch.qscqlmpa.dwitchgame.gamelifecycleevents.HostGameLifecycleEventRepository
import ch.qscqlmpa.dwitchmodel.game.RoomType
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import javax.inject.Inject

internal class ChangeCurrentRoomService @Inject constructor(
    private val store: InGameStore,
    private val hostGameLifecycleEventRepository: HostGameLifecycleEventRepository,
    private val guestGameLifecycleEventRepository: GuestGameLifecycleEventRepository
) {

    fun moveToGameRoom() {
        store.updateGameRoom(RoomType.GAME_ROOM)
        when (store.getLocalPlayerRole()) {
            PlayerRole.HOST -> hostGameLifecycleEventRepository.notify(HostGameLifecycleEvent.MovedToGameRoom)
            PlayerRole.GUEST -> guestGameLifecycleEventRepository.notify(GuestGameLifecycleEvent.MovedToGameRoom)
        }
    }
}
