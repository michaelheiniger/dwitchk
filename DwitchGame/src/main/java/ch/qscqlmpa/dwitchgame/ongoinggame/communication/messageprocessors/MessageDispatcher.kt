package ch.qscqlmpa.dwitchgame.ongoinggame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.model.EnvelopeReceived
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchgame.ongoinggame.di.OngoingGameScope
import io.reactivex.rxjava3.core.Completable
import org.tinylog.kotlin.Logger
import javax.inject.Inject
import javax.inject.Provider

@OngoingGameScope
class MessageDispatcher @Inject constructor(
    private val messageProcessors: @JvmSuppressWildcards Map<Class<out Message>, Provider<MessageProcessor>>
) {

    fun dispatch(envelope: EnvelopeReceived): Completable {
        val message = envelope.message
        val sender = envelope.senderId
        Logger.debug { "Dispatch message: $message" }
        return messageProcessors.getValue(message.javaClass).get().process(message, sender)
    }
}
