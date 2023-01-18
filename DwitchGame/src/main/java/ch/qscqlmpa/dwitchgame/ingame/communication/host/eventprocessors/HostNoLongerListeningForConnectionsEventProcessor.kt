package ch.qscqlmpa.dwitchgame.ingame.communication.host.eventprocessors

import ch.qscqlmpa.dwitchcommunication.ingame.websocket.ServerEvent
import ch.qscqlmpa.dwitchgame.ingame.communication.host.HostCommunicationStateRepository
import io.reactivex.rxjava3.core.Completable
import org.tinylog.kotlin.Logger
import javax.inject.Inject

internal class HostNoLongerListeningForConnectionsEventProcessor @Inject constructor(
    private val communicationStateRepository: HostCommunicationStateRepository
) : HostCommunicationEventProcessor {

    override fun process(event: ServerEvent.CommunicationEvent): Completable {

        event as ServerEvent.CommunicationEvent.StoppedListeningForConnections

        return Completable.fromAction {
            Logger.info { "No longer Listening for connections." }
            communicationStateRepository.notifyEvent(event)
        }
    }
}
