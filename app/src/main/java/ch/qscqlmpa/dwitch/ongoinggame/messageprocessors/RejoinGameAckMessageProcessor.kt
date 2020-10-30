package ch.qscqlmpa.dwitch.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionId
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import io.reactivex.Completable
import javax.inject.Inject

internal class RejoinGameAckMessageProcessor @Inject constructor() : MessageProcessor {

    override fun process(message: Message, senderLocalConnectionID: LocalConnectionId): Completable {
        return Completable.complete() // Nothing to do for the moment
    }
}