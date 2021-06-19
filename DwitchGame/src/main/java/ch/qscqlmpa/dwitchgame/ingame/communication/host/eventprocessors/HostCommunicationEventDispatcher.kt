package ch.qscqlmpa.dwitchgame.ingame.communication.host.eventprocessors

import ch.qscqlmpa.dwitchcommunication.websocket.server.ServerCommunicationEvent
import ch.qscqlmpa.dwitchgame.ingame.di.OngoingGameScope
import io.reactivex.rxjava3.core.Completable
import org.tinylog.kotlin.Logger
import javax.inject.Inject
import javax.inject.Provider

@OngoingGameScope
internal class HostCommunicationEventDispatcher @Inject constructor(
    private val eventProcessors: @JvmSuppressWildcards Map<Class<out ServerCommunicationEvent>,
        Provider<HostCommunicationEventProcessor>>
) {

    fun dispatch(event: ServerCommunicationEvent): Completable {
        Logger.debug { "Dispatching ServerCommunicationEvent $event (thread: ${Thread.currentThread().name})" }
        return eventProcessors.getValue(event.javaClass).get().process(event)
    }
}
