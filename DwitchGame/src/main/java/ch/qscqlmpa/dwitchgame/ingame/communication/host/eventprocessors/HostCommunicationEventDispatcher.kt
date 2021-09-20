package ch.qscqlmpa.dwitchgame.ingame.communication.host.eventprocessors

import ch.qscqlmpa.dwitchcommunication.ingame.websocket.ServerEvent
import ch.qscqlmpa.dwitchgame.ingame.communication.messageprocessors.MessageDispatcher
import ch.qscqlmpa.dwitchgame.ingame.di.OngoingGameScope
import io.reactivex.rxjava3.core.Completable
import org.tinylog.kotlin.Logger
import javax.inject.Inject
import javax.inject.Provider

@OngoingGameScope
internal class HostCommunicationEventDispatcher @Inject constructor(
    private val eventProcessors: @JvmSuppressWildcards Map<Class<out ServerEvent.CommunicationEvent>, Provider<HostCommunicationEventProcessor>>,
    private val messageDispatcher: MessageDispatcher
) {

    fun dispatch(event: ServerEvent): Completable {
        Logger.debug { "Dispatching $event (thread: ${Thread.currentThread().name})" }
        return when (event) {
            is ServerEvent.EnvelopeReceived -> messageDispatcher.dispatch(event.message, event.senderId)
            is ServerEvent.CommunicationEvent -> eventProcessors.getValue(event.javaClass).get().process(event)
        }
    }
}
