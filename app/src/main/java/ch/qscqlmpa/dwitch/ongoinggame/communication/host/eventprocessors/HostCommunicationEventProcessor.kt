package ch.qscqlmpa.dwitch.ongoinggame.communication.host.eventprocessors

import ch.qscqlmpa.dwitch.ongoinggame.communication.host.ServerCommunicationEvent
import io.reactivex.Completable

interface HostCommunicationEventProcessor {

    fun process(event: ServerCommunicationEvent): Completable
}