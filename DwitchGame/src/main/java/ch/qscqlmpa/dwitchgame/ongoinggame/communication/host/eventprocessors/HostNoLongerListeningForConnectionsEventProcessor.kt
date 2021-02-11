package ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.eventprocessors

import ch.qscqlmpa.dwitchcommunication.websocket.server.ServerCommunicationEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicationState
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicationStateRepository
import io.reactivex.rxjava3.core.Completable
import mu.KLogging
import javax.inject.Inject

internal class HostNoLongerListeningForConnectionsEventProcessor @Inject constructor(
    private val communicationStateRepository: HostCommunicationStateRepository
) : HostCommunicationEventProcessor {

    override fun process(event: ServerCommunicationEvent): Completable {

        event as ServerCommunicationEvent.NoLongerListeningForConnections

        return Completable.fromAction {
            logger.info { "No longer Listening for connections." }
            communicationStateRepository.updateState(HostCommunicationState.Closed)
        }
    }

    companion object : KLogging()
}
