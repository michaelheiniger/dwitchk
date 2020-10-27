package ch.qscqlmpa.dwitch.ongoinggame.communication.guest.eventprocessors

import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.ClientCommunicationEvent
import io.reactivex.Completable
import timber.log.Timber
import javax.inject.Inject

internal class GuestDisconnectedFromHostEventProcessor @Inject constructor() : GuestCommunicationEventProcessor {

    override fun process(event: ClientCommunicationEvent): Completable {
        Timber.d("Process GuestDisconnectedFromHostEvent")
        return Completable.complete()
    }
}