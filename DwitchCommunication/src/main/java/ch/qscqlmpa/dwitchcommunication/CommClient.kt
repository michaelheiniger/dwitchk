package ch.qscqlmpa.dwitchcommunication

import ch.qscqlmpa.dwitchcommunication.model.EnvelopeReceived
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.websocket.client.ClientCommunicationEvent
import io.reactivex.rxjava3.core.Observable

interface CommClient {

    fun start()

    fun stop()

    fun sendMessageToServer(message: Message)

    /**
     * Emits communication events. Stream never completes or throws any errors.
     */
    fun observeCommunicationEvents(): Observable<ClientCommunicationEvent>

    /**
     * Emits received messages. Stream never completes or throws any errors.
     */
    fun observeReceivedMessages(): Observable<EnvelopeReceived>
}