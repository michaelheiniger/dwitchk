package ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.EnvelopeToSend
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicator
import dagger.Lazy

internal abstract class BaseHostProcessor constructor(
        private val communicatorLazy: Lazy<HostCommunicator>
) : MessageProcessor {

    protected fun sendMessage(message: EnvelopeToSend) {
        return communicatorLazy.get().sendMessage(message)
    }

    protected fun sendMessages(messages: List<EnvelopeToSend>) {
        val communicator = communicatorLazy.get()
        messages.forEach { message -> communicator.sendMessage(message)}
    }

    protected fun closeConnectionWithGuest(connectionId: ConnectionId) {
        communicatorLazy.get().closeConnectionWithClient(connectionId)
    }
}