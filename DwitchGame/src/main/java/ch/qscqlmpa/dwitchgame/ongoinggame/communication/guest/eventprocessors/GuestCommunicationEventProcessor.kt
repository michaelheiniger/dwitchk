package ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.eventprocessors

import ch.qscqlmpa.dwitchcommunication.websocket.client.ClientCommunicationEvent
import io.reactivex.rxjava3.core.Completable

interface GuestCommunicationEventProcessor {

    fun process(event: ClientCommunicationEvent): Completable
}