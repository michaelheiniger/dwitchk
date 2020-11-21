package ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.model.EnvelopeToSend
import ch.qscqlmpa.dwitchcommunication.connectionstore.LocalConnectionId
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicator
import dagger.Lazy
import io.reactivex.Completable
import io.reactivex.Single

abstract class BaseHostProcessor constructor(
        private val communicatorLazy: Lazy<HostCommunicator>
) : MessageProcessor {

    protected fun sendMessage(message: EnvelopeToSend): Completable {
        return communicatorLazy.get().sendMessage(message)
    }

    protected fun sendMessages(messages: List<Single<EnvelopeToSend>>): Completable {
        val communicator = communicatorLazy.get()
        return Single.merge(messages)
                .concatMapCompletable(communicator::sendMessage)
    }

    protected fun closeConnectionWithGuest(connectionId: LocalConnectionId) {
        communicatorLazy.get().closeConnectionWithClient(connectionId)
    }
}