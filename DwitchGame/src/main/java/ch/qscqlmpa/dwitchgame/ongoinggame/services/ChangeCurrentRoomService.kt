package ch.qscqlmpa.dwitchgame.ongoinggame.services

import ch.qscqlmpa.dwitchgame.appevent.AppEvent
import ch.qscqlmpa.dwitchgame.appevent.AppEventRepository
import ch.qscqlmpa.dwitchmodel.game.RoomType
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

internal class ChangeCurrentRoomService @Inject constructor(
    private val store: InGameStore,
    private val appEventRepository: AppEventRepository,
) {

    fun moveToGameRoom(): Completable {
        return Completable.fromAction { store.updateGameRoom(RoomType.GAME_ROOM) }
            .doOnComplete { appEventRepository.notify(AppEvent.GameRoomJoinedByHost) }
    }
}