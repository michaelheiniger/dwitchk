package ch.qscqlmpa.dwitchgame.ongoinggame.communication.host

import ch.qscqlmpa.dwitchcommunication.model.EnvelopeReceived
import ch.qscqlmpa.dwitchcommunication.model.EnvelopeToSend
import ch.qscqlmpa.dwitchcommunication.websocket.server.ServerCommunicationEvent
import io.reactivex.rxjava3.core.Observable

interface ComputerCommunicator {
    fun observeMessagesForComputerPlayers(): Observable<EnvelopeToSend>
    fun sendMessageToHostFromComputerPlayer(envelope: EnvelopeReceived)
    fun sendCommunicationEventFromComputerPlayer(event: ServerCommunicationEvent)
}