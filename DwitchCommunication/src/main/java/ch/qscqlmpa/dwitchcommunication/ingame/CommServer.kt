package ch.qscqlmpa.dwitchcommunication.ingame

import ch.qscqlmpa.dwitchcommunication.ingame.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.ingame.model.Message
import ch.qscqlmpa.dwitchcommunication.ingame.model.Recipient
import ch.qscqlmpa.dwitchcommunication.ingame.websocket.ServerEvent
import io.reactivex.rxjava3.core.Observable

interface CommServer {

    fun start(ipAddress: String, port: Int)

    fun stop()

    fun sendMessage(message: Message, recipient: Recipient)

    fun observeEvents(): Observable<ServerEvent>

    fun closeConnectionWithClient(connectionId: ConnectionId)
}
