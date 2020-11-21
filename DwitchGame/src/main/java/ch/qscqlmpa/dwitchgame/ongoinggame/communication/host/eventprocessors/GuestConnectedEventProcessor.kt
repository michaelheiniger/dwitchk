package ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.eventprocessors

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionStore
import ch.qscqlmpa.dwitchcommunication.websocket.server.ServerCommunicationEvent
import io.reactivex.Completable
import timber.log.Timber
import javax.inject.Inject

internal class GuestConnectedEventProcessor @Inject constructor(
        private val connectionStore: ConnectionStore
) : HostCommunicationEventProcessor {

    override fun process(event: ServerCommunicationEvent): Completable {

        event as ServerCommunicationEvent.ClientConnected

        val address = connectionStore.getAddress(event.localConnectionId)
        Timber.i("Client connected with address: $address")

        // Nothing to do
        return Completable.complete()
    }
}