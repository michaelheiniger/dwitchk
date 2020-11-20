package ch.qscqlmpa.dwitch.ongoinggame.communication.host.eventprocessors

import ch.qscqlmpa.dwitch.ongoinggame.communication.host.HostCommunicationState
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.ServerCommunicationEvent
import ch.qscqlmpa.dwitch.ongoinggame.events.HostCommunicationStateRepository
import io.reactivex.Completable
import javax.inject.Inject

internal class HostListeningForConnectionsEventProcessor @Inject constructor(
    private val communicationStateRepository: HostCommunicationStateRepository
) : HostCommunicationEventProcessor {

    override fun process(event: ServerCommunicationEvent): Completable {
        return Completable.fromAction { communicationStateRepository.notify(HostCommunicationState.Open) }
    }
}