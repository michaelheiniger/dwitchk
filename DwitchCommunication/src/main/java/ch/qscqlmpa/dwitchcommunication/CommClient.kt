package ch.qscqlmpa.dwitchcommunication

import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.websocket.ClientEvent
import io.reactivex.rxjava3.core.Observable

interface CommClient {

    fun start()

    fun stop()

    fun sendMessageToServer(message: Message)

    /**
     * Emits communication events. Stream never completes or throws any errors.
     */
    fun observeCommunicationEvents(): Observable<ClientEvent>
}
