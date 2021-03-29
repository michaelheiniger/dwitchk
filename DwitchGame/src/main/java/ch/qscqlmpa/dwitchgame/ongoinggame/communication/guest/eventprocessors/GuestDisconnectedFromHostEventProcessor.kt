package ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.eventprocessors

import ch.qscqlmpa.dwitchcommunication.websocket.client.ClientCommunicationEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicationState
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicationStateRepository
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicator
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import io.reactivex.rxjava3.core.Completable
import org.tinylog.kotlin.Logger
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
        Logger.info { "Process GuestDisconnectedFromHostEvent" }
        return Completable.fromAction {
            commStateRepository.updateState(GuestCommunicationState.Disconnected)
            communicator.disconnect()
            store.setAllPlayersToDisconnected()
        }
    }
}
