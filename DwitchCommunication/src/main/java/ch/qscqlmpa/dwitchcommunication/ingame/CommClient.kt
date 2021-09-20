package ch.qscqlmpa.dwitchcommunication.ingame

import ch.qscqlmpa.dwitchcommunication.ingame.model.Message
import ch.qscqlmpa.dwitchcommunication.ingame.websocket.ClientEvent
import io.reactivex.rxjava3.core.Observable

interface CommClient {

    fun start(ipAddress: String, port: Int)

    fun stop()

    fun sendMessageToServer(message: Message)

    /**
     * Emits communication events. Stream never completes or throws any errors.
     */
    fun observeCommunicationEvents(): Observable<ClientEvent>
}
