package ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.LocalConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import io.reactivex.rxjava3.core.Completable

interface MessageProcessor {

    fun process(message: Message, senderLocalConnectionID: LocalConnectionId): Completable
}