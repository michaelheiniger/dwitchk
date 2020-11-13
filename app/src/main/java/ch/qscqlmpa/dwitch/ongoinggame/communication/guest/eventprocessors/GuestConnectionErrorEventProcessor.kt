package ch.qscqlmpa.dwitch.ongoinggame.communication.guest.eventprocessors

import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.ClientCommunicationEvent
import ch.qscqlmpa.dwitch.ongoinggame.events.GuestCommunicationEventRepository
import ch.qscqlmpa.dwitch.ongoinggame.events.GuestCommunicationState
import io.reactivex.Completable
import timber.log.Timber
import javax.inject.Inject

internal class GuestConnectionErrorEventProcessor @Inject constructor(
    private val commEventRepository: GuestCommunicationEventRepository,
) : GuestCommunicationEventProcessor {

    override fun process(event: ClientCommunicationEvent): Completable {
        Timber.d("Process DisconnectedFromHost")
        return Completable.fromAction { commEventRepository.notify(GuestCommunicationState.Error) }
    }
}