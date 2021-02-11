package ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.eventprocessors

import ch.qscqlmpa.dwitchcommunication.websocket.client.ClientCommunicationEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.di.OngoingGameScope
import io.reactivex.rxjava3.core.Completable
import mu.KLogging
import javax.inject.Inject
import javax.inject.Provider

@OngoingGameScope
internal class GuestCommunicationEventDispatcher @Inject constructor(
    private val eventProcessors: @JvmSuppressWildcards Map<Class<out ClientCommunicationEvent>,
        Provider<GuestCommunicationEventProcessor>>
) {

    fun dispatch(event: ClientCommunicationEvent): Completable {
        logger.debug { "Dispatching ClientCommunicationEvent ${event.javaClass}" }
        return eventProcessors.getValue(event.javaClass).get().process(event)
    }

    companion object : KLogging()
}
