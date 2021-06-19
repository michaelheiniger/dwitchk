package ch.qscqlmpa.dwitchgame.ingame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.model.EnvelopeReceived
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchgame.ingame.di.OngoingGameScope
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
        val processor = messageProcessors.getValue(message.javaClass).get()
        Logger.debug { "Dispatch message: $message to $processor" }
        return processor.process(message, sender)
    }
}
