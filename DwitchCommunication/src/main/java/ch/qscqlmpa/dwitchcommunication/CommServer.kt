package ch.qscqlmpa.dwitchcommunication

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.EnvelopeReceived
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.model.Recipient
import ch.qscqlmpa.dwitchcommunication.websocket.server.ServerCommunicationEvent
import io.reactivex.rxjava3.core.Observable

interface CommServer {

    fun start()

    fun stop()

    fun sendMessage(message: Message, recipient: Recipient)

    fun observeCommunicationEvents(): Observable<ServerCommunicationEvent>

    fun observeReceivedMessages(): Observable<EnvelopeReceived>

    fun closeConnectionWithClient(connectionId: ConnectionId)
}
