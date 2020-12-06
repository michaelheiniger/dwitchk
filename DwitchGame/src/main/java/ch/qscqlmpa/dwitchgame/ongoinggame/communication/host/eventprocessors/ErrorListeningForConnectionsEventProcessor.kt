package ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.eventprocessors

import ch.qscqlmpa.dwitchcommunication.websocket.server.ServerCommunicationEvent
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

internal class ErrorListeningForConnectionsEventProcessor @Inject constructor(

) : HostCommunicationEventProcessor {

    override fun process(event: ServerCommunicationEvent): Completable {
        return Completable.complete() // Nothing to do
    }
}