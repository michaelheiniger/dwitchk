package ch.qscqlmpa.dwitchcommunication

import ch.qscqlmpa.dwitchcommunication.connectionstore.LocalConnectionId
import ch.qscqlmpa.dwitchcommunication.model.EnvelopeReceived
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.websocket.server.ServerCommunicationEvent
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable

interface CommServer {

    fun start()

    fun stop()

    fun sendMessage(message: Message, recipientAddress: AddressType): Completable

    fun observeCommunicationEvents(): Observable<ServerCommunicationEvent>

    fun observeReceivedMessages(): Observable<EnvelopeReceived>

    fun closeConnectionWithClient(localConnectionId: LocalConnectionId)
}