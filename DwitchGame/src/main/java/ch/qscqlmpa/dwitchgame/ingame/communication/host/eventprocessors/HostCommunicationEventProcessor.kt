package ch.qscqlmpa.dwitchgame.ingame.communication.host.eventprocessors

import ch.qscqlmpa.dwitchcommunication.ingame.websocket.ServerEvent
import io.reactivex.rxjava3.core.Completable

internal interface HostCommunicationEventProcessor {

    fun process(event: ServerEvent.CommunicationEvent): Completable
}
