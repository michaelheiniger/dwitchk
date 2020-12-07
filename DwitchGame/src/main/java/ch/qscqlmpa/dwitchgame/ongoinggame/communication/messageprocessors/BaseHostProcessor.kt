package ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.EnvelopeToSend
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicator
import dagger.Lazy
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Single

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

    protected fun closeConnectionWithGuest(connectionId: ConnectionId) {
        communicatorLazy.get().closeConnectionWithClient(connectionId)
    }
}