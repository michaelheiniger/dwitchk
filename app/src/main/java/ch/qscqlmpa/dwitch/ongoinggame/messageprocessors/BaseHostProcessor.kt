package ch.qscqlmpa.dwitch.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitch.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.messages.EnvelopeToSend
import dagger.Lazy
import io.reactivex.Completable
import io.reactivex.Single

abstract class BaseHostProcessor constructor(
        private val communicatorLazy: Lazy<HostCommunicator>
) : MessageProcessor {

    protected fun sendMessages(messages: List<Single<EnvelopeToSend>>): Completable {
        val communicator = communicatorLazy.get()
        return Single.merge(messages)
                .concatMapCompletable(communicator::sendMessage)
    }
}