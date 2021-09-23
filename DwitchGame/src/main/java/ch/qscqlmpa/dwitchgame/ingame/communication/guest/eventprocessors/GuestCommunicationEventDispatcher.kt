package ch.qscqlmpa.dwitchgame.ingame.communication.guest.eventprocessors

import ch.qscqlmpa.dwitchcommunication.ingame.websocket.ClientEvent
import ch.qscqlmpa.dwitchgame.ingame.communication.messageprocessors.MessageDispatcher
import ch.qscqlmpa.dwitchgame.ingame.di.InGameScope
import io.reactivex.rxjava3.core.Completable
import org.tinylog.kotlin.Logger
import javax.inject.Inject
import javax.inject.Provider

@InGameScope
internal class GuestCommunicationEventDispatcher @Inject constructor(
    private val eventProcessors: @JvmSuppressWildcards Map<Class<out ClientEvent.CommunicationEvent>, Provider<GuestCommunicationEventProcessor>>,
    private val messageDispatcher: MessageDispatcher
) {

    fun dispatch(event: ClientEvent): Completable {
        Logger.debug { "Dispatching $event (thread: ${Thread.currentThread().name})" }
        return when (event) {
            is ClientEvent.EnvelopeReceived -> messageDispatcher.dispatch(event.message, event.senderId)
            is ClientEvent.CommunicationEvent -> eventProcessors.getValue(event.javaClass).get().process(event)
        }
    }
}
