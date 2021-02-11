package ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.eventprocessors

import ch.qscqlmpa.dwitchcommunication.websocket.server.ServerCommunicationEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicatorImpl
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicationState
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicationStateRepository
import io.reactivex.rxjava3.core.Completable
import javax.inject.Inject

internal class ErrorListeningForConnectionsEventProcessor @Inject constructor(
    private val communicationStateRepository: HostCommunicationStateRepository
) : HostCommunicationEventProcessor {

    override fun process(event: ServerCommunicationEvent): Completable {

        event as ServerCommunicationEvent.ErrorListeningForConnections

        return Completable.fromAction {
            GuestCommunicatorImpl.logger.error { "Error listening for connections: ${event.exception}" }
            communicationStateRepository.updateState(HostCommunicationState.Error)
        }
    }
}
