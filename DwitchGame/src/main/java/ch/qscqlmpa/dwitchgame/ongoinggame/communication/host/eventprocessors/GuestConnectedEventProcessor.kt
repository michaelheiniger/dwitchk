package ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.eventprocessors

import ch.qscqlmpa.dwitchcommunication.websocket.server.ServerCommunicationEvent
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

internal class GuestConnectedEventProcessor @Inject constructor() : HostCommunicationEventProcessor {

    override fun process(event: ServerCommunicationEvent): Completable {

        event as ServerCommunicationEvent.ClientConnected

        // Nothing to do
        return Completable.complete()
    }
}