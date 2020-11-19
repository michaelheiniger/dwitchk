package ch.qscqlmpa.dwitch.ongoinggame.communication.guest.eventprocessors

import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.ClientCommunicationEvent
import ch.qscqlmpa.dwitch.ongoinggame.OngoingGameScope
import io.reactivex.Completable
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Provider

@OngoingGameScope
class GuestCommunicationEventDispatcher @Inject constructor(
        private val eventProcessors: @JvmSuppressWildcards Map<Class<out ClientCommunicationEvent>,
                Provider<GuestCommunicationEventProcessor>>
) {

    fun dispatch(event: ClientCommunicationEvent): Completable {
        Timber.d("Dispatching ClientCommunicationEvent %s", event.javaClass)
        return eventProcessors.getValue(event.javaClass).get().process(event)
    }
}