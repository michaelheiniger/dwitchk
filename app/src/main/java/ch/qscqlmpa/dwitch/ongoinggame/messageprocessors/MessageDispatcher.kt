package ch.qscqlmpa.dwitch.ongoinggame.messageprocessors

import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import ch.qscqlmpa.dwitch.ongoinggame.messages.EnvelopeReceived
import ch.qscqlmpa.dwitch.service.OngoingGameScope
import io.reactivex.Completable
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider

@OngoingGameScope
class MessageDispatcher @Inject constructor(
        private val messageProcessors: @JvmSuppressWildcards Map<Class<out Message>, Provider<MessageProcessor>>
) {

    fun dispatch(envelope: EnvelopeReceived): Completable {
        val message = envelope.message
        val sender = envelope.senderId
        Timber.d("Dispatch message: $message")
        return messageProcessors.getValue(message.javaClass).get().process(message, sender)
    }
}