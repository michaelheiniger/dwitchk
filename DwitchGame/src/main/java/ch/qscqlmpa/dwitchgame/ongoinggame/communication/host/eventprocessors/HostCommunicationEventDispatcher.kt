package ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.eventprocessors


import ch.qscqlmpa.dwitchcommunication.websocket.server.ServerCommunicationEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.di.OngoingGameScope
import io.reactivex.rxjava3.core.Completable
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider

@OngoingGameScope
internal class HostCommunicationEventDispatcher @Inject constructor(
        private val eventProcessors: @JvmSuppressWildcards Map<Class<out ServerCommunicationEvent>,
                Provider<HostCommunicationEventProcessor>>
) {

    fun dispatch(event: ServerCommunicationEvent): Completable {
        Timber.d("Dispatching ServerCommunicationEvent $event (thread: ${Thread.currentThread().name})")
        return eventProcessors.getValue(event.javaClass).get().process(event)
    }
}