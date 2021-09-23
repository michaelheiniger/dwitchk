package ch.qscqlmpa.dwitchgame.ingame.communication.messageprocessors

import ch.qscqlmpa.dwitchcommunication.ingame.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.ingame.model.Message
import ch.qscqlmpa.dwitchgame.ingame.di.InGameScope
import io.reactivex.rxjava3.core.Completable
import org.tinylog.kotlin.Logger
import javax.inject.Inject
import javax.inject.Provider

@InGameScope
class MessageDispatcher @Inject constructor(
    private val messageProcessors: @JvmSuppressWildcards Map<Class<out Message>, Provider<MessageProcessor>>
) {

    fun dispatch(message: Message, sender: ConnectionId): Completable {
        Logger.debug { "Dispatch message: $message" }
        return messageProcessors.getValue(message.javaClass).get().process(message, sender)
    }
}
