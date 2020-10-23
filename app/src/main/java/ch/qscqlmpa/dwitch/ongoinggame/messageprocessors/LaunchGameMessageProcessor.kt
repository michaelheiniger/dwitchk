package ch.qscqlmpa.dwitch.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionId
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import ch.qscqlmpa.dwitch.ongoinggame.persistence.InGameStore
import io.reactivex.Completable
import javax.inject.Inject

class LaunchGameMessageProcessor @Inject constructor(private val store: InGameStore) :
    MessageProcessor {

    override fun process(message: Message, senderLocalConnectionID: LocalConnectionId): Completable {

        val msg = message as Message.LaunchGameMessage

        return Completable.fromCallable {
            store.updateGameState(msg.gameState)
        }
    }
}