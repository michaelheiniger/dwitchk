package ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.eventprocessors

import ch.qscqlmpa.dwitchcommunication.websocket.server.ServerCommunicationEvent
import io.reactivex.rxjava3.core.Completable
import timber.log.Timber
import javax.inject.Inject

internal class ErrorListeningForConnectionsEventProcessor @Inject constructor(

) : HostCommunicationEventProcessor {

    override fun process(event: ServerCommunicationEvent): Completable {
        event as ServerCommunicationEvent.ErrorListeningForConnections
        Timber.e("Error listening for connections: ${event.exception}")
        return Completable.complete() // Nothing to do
    }
}