package ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.eventprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchcommunication.websocket.server.ServerCommunicationEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicationState
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicationStateRepository
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.rxjava3.core.Completable
import timber.log.Timber
import javax.inject.Inject

internal class HostListeningForConnectionsEventProcessor @Inject constructor(
    private val store: InGameStore,
    private val connectionStore: ConnectionStore,
    private val communicationStateRepository: HostCommunicationStateRepository
) : HostCommunicationEventProcessor {

    override fun process(event: ServerCommunicationEvent): Completable {

        event as ServerCommunicationEvent.ListeningForConnections

        return Completable.fromAction {
            Timber.i("Listening for connections...")
            val hostDwitchId = store.getLocalPlayerDwitchId()
            Timber.d("pair host connection ID ${event.hostConnectionId} to its Dwitch id: $hostDwitchId")
            connectionStore.pairConnectionWithPlayer(event.hostConnectionId, hostDwitchId)
            communicationStateRepository.updateState(HostCommunicationState.Open)
        }
    }
}
