package ch.qscqlmpa.dwitch.ongoinggame.communication.host.eventprocessors

import ch.qscqlmpa.dwitch.ongoinggame.communication.host.ServerCommunicationEvent
import ch.qscqlmpa.dwitch.service.OngoingGameScope
import io.reactivex.Completable
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider

@OngoingGameScope
class HostCommunicationEventDispatcher @Inject constructor(
        private val eventProcessors: @JvmSuppressWildcards Map<Class<out ServerCommunicationEvent>,
                Provider<HostCommunicationEventProcessor>>
) {

    fun dispatch(event: ServerCommunicationEvent): Completable {
        Timber.d("Dispatching ServerCommunicationEvent %s", event)
        return eventProcessors.getValue(event.javaClass).get().process(event)
    }
}