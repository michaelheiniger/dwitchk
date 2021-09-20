package ch.qscqlmpa.dwitchgame.ingame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.ingame.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.ingame.model.Message
import io.reactivex.rxjava3.core.Completable

interface MessageProcessor {

    fun process(message: Message, senderConnectionID: ConnectionId): Completable
}
