package ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

internal class RejoinGameAckMessageProcessor @Inject constructor() : MessageProcessor {

    override fun process(message: Message, senderConnectionID: ConnectionId): Completable {
        return Completable.complete() // Nothing to do for the moment
    }
}