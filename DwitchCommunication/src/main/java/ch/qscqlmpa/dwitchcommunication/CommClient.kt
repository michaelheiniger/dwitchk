package ch.qscqlmpa.dwitchcommunication

import ch.qscqlmpa.dwitchcommunication.model.EnvelopeReceived
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.websocket.client.ClientCommunicationEvent
import io.reactivex.Completable
import io.reactivex.Observable

interface CommClient {

    fun start()

    fun stop()

    fun sendMessage(message: Message): Completable //TODO: Add recipientAddress (even if it's always the Host, for consistency)

    /**
     * Emits communication events. Stream never completes or throws any errors.
     */
    fun observeCommunicationEvents(): Observable<ClientCommunicationEvent>

    /**
     * Emits received messages. Stream never completes or throws any errors.
     */
    fun observeReceivedMessages(): Observable<EnvelopeReceived>
}