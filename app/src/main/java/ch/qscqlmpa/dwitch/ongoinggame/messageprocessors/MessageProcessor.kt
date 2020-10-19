package ch.qscqlmpa.dwitch.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionId
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import io.reactivex.Completable

interface MessageProcessor {

    fun process(message: Message, senderLocalConnectionID: LocalConnectionId): Completable
}