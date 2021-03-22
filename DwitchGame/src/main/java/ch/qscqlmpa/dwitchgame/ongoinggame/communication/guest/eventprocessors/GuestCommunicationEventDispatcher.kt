package ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.eventprocessors

import ch.qscqlmpa.dwitchcommunication.websocket.client.ClientCommunicationEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.di.OngoingGameScope
import io.reactivex.rxjava3.core.Completable
import org.tinylog.kotlin.Logger
import javax.inject.Inject
import javax.inject.Provider

@OngoingGameScope
internal class GuestCommunicationEventDispatcher @Inject constructor(
    private val eventProcessors: @JvmSuppressWildcards Map<Class<out ClientCommunicationEvent>,
        Provider<GuestCommunicationEventProcessor>>
) {

    fun dispatch(event: ClientCommunicationEvent): Completable {
        Logger.debug { "Dispatching ClientCommunicationEvent ${event.javaClass}" }
        return eventProcessors.getValue(event.javaClass).get().process(event)
    }
}
