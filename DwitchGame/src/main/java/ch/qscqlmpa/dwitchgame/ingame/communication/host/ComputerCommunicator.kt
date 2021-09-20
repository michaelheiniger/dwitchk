package ch.qscqlmpa.dwitchgame.ingame.communication.host

import ch.qscqlmpa.dwitchcommunication.ingame.model.EnvelopeToSend
import ch.qscqlmpa.dwitchcommunication.ingame.websocket.ServerEvent
import io.reactivex.rxjava3.core.Observable

interface ComputerCommunicator {
    fun observeMessagesForComputerPlayers(): Observable<EnvelopeToSend>
    fun sendMessageToHostFromComputerPlayer(envelope: ServerEvent.EnvelopeReceived)
    fun sendCommunicationEventFromComputerPlayer(event: ServerEvent.CommunicationEvent)
}
