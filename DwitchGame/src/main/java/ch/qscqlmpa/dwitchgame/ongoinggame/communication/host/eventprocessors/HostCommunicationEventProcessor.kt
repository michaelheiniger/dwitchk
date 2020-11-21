package ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.eventprocessors

import ch.qscqlmpa.dwitchcommunication.websocket.server.ServerCommunicationEvent
import io.reactivex.Completable

interface HostCommunicationEventProcessor {

    fun process(event: ServerCommunicationEvent): Completable
}