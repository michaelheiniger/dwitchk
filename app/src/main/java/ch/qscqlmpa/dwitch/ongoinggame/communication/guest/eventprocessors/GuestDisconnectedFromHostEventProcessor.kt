package ch.qscqlmpa.dwitch.ongoinggame.communication.guest.eventprocessors

import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.ClientCommunicationEvent
import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.events.GuestCommunicationStateRepository
import ch.qscqlmpa.dwitch.ongoinggame.events.GuestCommunicationState
import ch.qscqlmpa.dwitch.ongoinggame.persistence.InGameStore
import io.reactivex.Completable
import timber.log.Timber
import javax.inject.Inject

/**
 * Occurs if the host closes the connection or if the connection is broken.
 * --> it does NOT occur if the guest closes the connection itself (e.g. by leaving the game).
 */
internal class GuestDisconnectedFromHostEventProcessor @Inject constructor(
    private val store: InGameStore,
    private val commStateRepository: GuestCommunicationStateRepository,
    private val communicator: GuestCommunicator
) : GuestCommunicationEventProcessor {

    override fun process(event: ClientCommunicationEvent): Completable {
        Timber.i("Process GuestDisconnectedFromHostEvent")
        return Completable.fromAction {
            commStateRepository.notify(GuestCommunicationState.Disconnected)
            communicator.closeConnection()
            store.setAllPlayersToDisconnected()
        }
    }
}