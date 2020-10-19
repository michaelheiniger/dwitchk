package ch.qscqlmpa.dwitch.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitch.ongoinggame.InGameStore
import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionId
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
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