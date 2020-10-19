package ch.qscqlmpa.dwitch.communication.server

import ch.qscqlmpa.dwitch.GuestIdTestHost
import ch.qscqlmpa.dwitch.ongoinggame.messages.EnvelopeToSend
import io.reactivex.Observable

interface ServerTestStub {

    fun connectClientToServer(connectionInitiator: GuestIdTestHost, enableThreadBreak: Boolean = false)

    fun guestSendsMessageToServer(sender: GuestIdTestHost, envelopeToSend: EnvelopeToSend, enableThreadBreak: Boolean = false)

    fun observeMessagesSent(): Observable<String>

    fun observeMessagesBroadcasted(): Observable<String>

    fun disconnectFromServer(guestIdentifier: GuestIdTestHost, enableThreadBreak: Boolean = false)
}