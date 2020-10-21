package ch.qscqlmpa.dwitch.ongoinggame.communication.host

import ch.qscqlmpa.dwitch.ongoinggame.communication.websocket.AddressType
import ch.qscqlmpa.dwitch.ongoinggame.communication.LocalConnectionId
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import ch.qscqlmpa.dwitch.ongoinggame.messages.EnvelopeReceived
import io.reactivex.Completable
import io.reactivex.Observable

interface CommServer {

    fun start()

    fun stop()

    fun sendMessage(message: Message, recipientAddress: AddressType): Completable

    fun observeCommunicationEvents(): Observable<ServerCommunicationEvent>

    fun observeReceivedMessages(): Observable<EnvelopeReceived>

    fun closeConnectionWithClient(localConnectionId: LocalConnectionId)
}