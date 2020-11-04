package ch.qscqlmpa.dwitch.ongoinggame.communication.host

import ch.qscqlmpa.dwitch.PlayerHostTest
import ch.qscqlmpa.dwitch.ongoinggame.messages.EnvelopeToSend
import io.reactivex.Observable

interface ServerTestStub {

    fun connectClientToServer(connectionInitiator: PlayerHostTest, enableThreadBreak: Boolean = false)

    fun guestSendsMessageToServer(sender: PlayerHostTest, envelopeToSend: EnvelopeToSend, enableThreadBreak: Boolean = false)

    fun observeMessagesSent(): Observable<String>

    fun observeMessagesBroadcasted(): Observable<String>

    fun disconnectFromServer(guestIdentifier: PlayerHostTest, enableThreadBreak: Boolean = false)
}