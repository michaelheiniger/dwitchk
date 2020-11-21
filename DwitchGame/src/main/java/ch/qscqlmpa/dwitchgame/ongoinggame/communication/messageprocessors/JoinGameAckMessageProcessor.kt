package ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.LocalConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.Completable
import javax.inject.Inject

internal class JoinGameAckMessageProcessor @Inject constructor(private val store: InGameStore) :
    MessageProcessor {

    override fun process(message: Message, senderLocalConnectionID: LocalConnectionId): Completable {

        val msg = message as Message.JoinGameAckMessage

        return Completable.fromCallable {
            store.updateGameWithCommonId(msg.gameCommonId)
            store.updateLocalPlayerWithInGameId(msg.playerInGameId)
        }
    }
}