package ch.qscqlmpa.dwitchgame.ingame.communication.guest.eventprocessors

import ch.qscqlmpa.dwitchcommunication.ingame.websocket.ClientEvent
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationStateRepository
import io.reactivex.rxjava3.core.Completable

internal abstract class BaseGuestCommunicationEventProcessor(
    private val commStateRepository: GuestCommunicationStateRepository
) : GuestCommunicationEventProcessor {

    override fun process(event: ClientEvent.CommunicationEvent): Completable {
        return Completable.fromAction { commStateRepository.notifyEvent(event) }
    }
}
