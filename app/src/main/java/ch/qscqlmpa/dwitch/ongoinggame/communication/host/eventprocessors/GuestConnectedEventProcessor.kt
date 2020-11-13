package ch.qscqlmpa.dwitch.ongoinggame.communication.host.eventprocessors

import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionIdStore
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.ServerCommunicationEvent
import io.reactivex.Completable
import timber.log.Timber
import javax.inject.Inject

internal class GuestConnectedEventProcessor @Inject constructor(
        private val localConnectionIdStore: LocalConnectionIdStore
) : HostCommunicationEventProcessor {

    override fun process(event: ServerCommunicationEvent): Completable {

        event as ServerCommunicationEvent.ClientConnected

        val address = localConnectionIdStore.getAddress(event.localConnectionId)
        Timber.i("Client connected with address: %s", address)

        // Nothing to do
        return Completable.complete()
    }
}