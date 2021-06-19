package ch.qscqlmpa.dwitchgame.ingame.communication.guest.eventprocessors

import ch.qscqlmpa.dwitchcommunication.websocket.client.ClientCommunicationEvent
import io.reactivex.rxjava3.core.Completable

internal interface GuestCommunicationEventProcessor {

    fun process(event: ClientCommunicationEvent): Completable
}
