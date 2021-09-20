package ch.qscqlmpa.dwitchgame.ingame.communication.guest.eventprocessors

import ch.qscqlmpa.dwitchcommunication.ingame.websocket.ClientEvent
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationState
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationStateRepository
import io.reactivex.rxjava3.core.Completable
import org.tinylog.kotlin.Logger
import javax.inject.Inject

/**
 * Occurs if the host closes the connection or if the connection is broken.
 * --> it does NOT occur if the guest closes the connection itself (e.g. by leaving the game).
 */
internal class GuestDisconnectedFromHostEventProcessor @Inject constructor(
    private val commStateRepository: GuestCommunicationStateRepository
) : GuestCommunicationEventProcessor {

    override fun process(event: ClientEvent.CommunicationEvent): Completable {
        Logger.info { "Process GuestDisconnectedFromHostEvent" }
        return Completable.fromAction {
            commStateRepository.updateState(GuestCommunicationState.Disconnected)
        }
    }
}
