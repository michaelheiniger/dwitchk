package ch.qscqlmpa.dwitch.ongoinggame.communication.guest.eventprocessors

import ch.qscqlmpa.dwitch.ongoinggame.communication.guest.ClientCommunicationEvent
import io.reactivex.Completable

interface GuestCommunicationEventProcessor {

    fun process(event: ClientCommunicationEvent): Completable
}