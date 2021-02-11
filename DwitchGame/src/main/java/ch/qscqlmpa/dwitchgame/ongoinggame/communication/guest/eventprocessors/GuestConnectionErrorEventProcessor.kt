package ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.eventprocessors

import ch.qscqlmpa.dwitchcommunication.websocket.client.ClientCommunicationEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicationState
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicationStateRepository
import io.reactivex.rxjava3.core.Completable
import mu.KLogging
import javax.inject.Inject

internal class GuestConnectionErrorEventProcessor @Inject constructor(
    private val commStateRepository: GuestCommunicationStateRepository,
) : GuestCommunicationEventProcessor {

    override fun process(event: ClientCommunicationEvent): Completable {
        logger.debug { "Process DisconnectedFromHost" }
        return Completable.fromAction { commStateRepository.notify(GuestCommunicationState.Error) }
    }

    companion object : KLogging()
}
