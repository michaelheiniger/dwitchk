package ch.qscqlmpa.dwitchgame.ingame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.ingame.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.ingame.model.Message
import ch.qscqlmpa.dwitchgame.gamelifecycle.GuestGameLifecycleEvent
import ch.qscqlmpa.dwitchgame.gamelifecycle.GuestGameLifecycleEventRepository
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

internal class JoinGameAckMessageProcessor @Inject constructor(
    private val store: InGameStore,
    private val guestGameLifecycleEventRepository: GuestGameLifecycleEventRepository
) : MessageProcessor {

    override fun process(message: Message, senderConnectionID: ConnectionId): Completable {

        val msg = message as Message.JoinGameAckMessage

        return Completable.fromCallable {
            store.updateGameWithCommonId(msg.gameCommonId)
            store.updateLocalPlayerWithDwitchId(msg.playerId)
            guestGameLifecycleEventRepository.notify(GuestGameLifecycleEvent.GameJoined)
        }
    }
}
