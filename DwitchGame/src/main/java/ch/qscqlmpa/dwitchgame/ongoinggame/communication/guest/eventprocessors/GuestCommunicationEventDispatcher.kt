package ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.eventprocessors


import ch.qscqlmpa.dwitchcommunication.websocket.client.ClientCommunicationEvent
import ch.qscqlmpa.dwitchgame.di.GameScope
import ch.qscqlmpa.dwitchgame.ongoinggame.di.OngoingGameScope
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
        Timber.d("Dispatching ClientCommunicationEvent ${event.javaClass}")
        return eventProcessors.getValue(event.javaClass).get().process(event)
    }
}