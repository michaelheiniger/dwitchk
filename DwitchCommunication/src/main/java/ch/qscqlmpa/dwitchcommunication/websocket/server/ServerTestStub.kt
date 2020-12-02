package ch.qscqlmpa.dwitchcommunication.websocket.server

import ch.qscqlmpa.dwitchcommunication.model.EnvelopeToSend
import ch.qscqlmpa.dwitchcommunication.websocket.PlayerHostTest
import io.reactivex.rxjava3.core.Observable

interface ServerTestStub {

    fun connectClientToServer(connectionInitiator: PlayerHostTest, enableThreadBreak: Boolean = false)

    fun guestSendsMessageToServer(sender: PlayerHostTest, envelopeToSend: EnvelopeToSend, enableThreadBreak: Boolean = false)

    fun observeMessagesSent(): Observable<String>

    fun observeMessagesBroadcasted(): Observable<String>

    fun disconnectFromServer(guestIdentifier: PlayerHostTest, enableThreadBreak: Boolean = false)
}