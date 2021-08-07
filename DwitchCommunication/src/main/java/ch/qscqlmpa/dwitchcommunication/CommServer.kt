package ch.qscqlmpa.dwitchcommunication

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.model.Recipient
import ch.qscqlmpa.dwitchcommunication.websocket.ServerEvent
import io.reactivex.rxjava3.core.Observable

interface CommServer {

    fun start()

    fun stop()

    fun sendMessage(message: Message, recipient: Recipient)

    fun observeEvents(): Observable<ServerEvent>

    fun closeConnectionWithClient(connectionId: ConnectionId)
}
