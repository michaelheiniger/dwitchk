package ch.qscqlmpa.dwitch.ongoinggame.communication.guest

import ch.qscqlmpa.dwitch.ongoinggame.messages.EnvelopeReceived
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
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