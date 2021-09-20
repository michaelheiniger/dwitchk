package ch.qscqlmpa.dwitchgame.ingame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.ingame.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.ingame.model.Message
import ch.qscqlmpa.dwitchgame.gamelifecycle.GuestGameLifecycleEvent
import ch.qscqlmpa.dwitchgame.gamelifecycle.GuestGameLifecycleEventRepository
import ch.qscqlmpa.dwitchgame.ingame.gameevents.GuestGameEvent
import ch.qscqlmpa.dwitchgame.ingame.gameevents.GuestGameEventRepository
import ch.qscqlmpa.dwitchmodel.game.RoomType
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

internal class LaunchGameMessageProcessor @Inject constructor(
    private val store: InGameStore,
    private val gameLifecycleEventRepository: GuestGameLifecycleEventRepository,
    private val gameEventRepository: GuestGameEventRepository
) : MessageProcessor {

    override fun process(message: Message, senderConnectionID: ConnectionId): Completable {

        message as Message.LaunchGameMessage

        return Completable.fromAction {
            store.updateCurrentRoom(RoomType.GAME_ROOM)
            store.updateGameState(message.gameState)
            gameLifecycleEventRepository.notify(GuestGameLifecycleEvent.MovedToGameRoom)
            gameEventRepository.notify(GuestGameEvent.GameLaunched)
        }
    }
}
