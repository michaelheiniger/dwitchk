package ch.qscqlmpa.dwitchgame.ingame.communication.guest.eventprocessors

import ch.qscqlmpa.dwitchcommunication.ingame.websocket.ClientEvent
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationState
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationStateRepository
import io.reactivex.rxjava3.core.Completable
import org.tinylog.kotlin.Logger
import javax.inject.Inject

internal class GuestStoppedEventProcessor @Inject constructor(
    private val commStateRepository: GuestCommunicationStateRepository,
) : GuestCommunicationEventProcessor {

    override fun process(event: ClientEvent.CommunicationEvent): Completable {
        Logger.debug { "Process GuestStoppedEventProcessor" }
        return Completable.fromAction { commStateRepository.updateState(GuestCommunicationState.Disconnected) }
    }
}
