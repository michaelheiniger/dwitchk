package ch.qscqlmpa.dwitchcommunication.websocket.server.test

import ch.qscqlmpa.dwitchcommunication.model.Message
import io.reactivex.rxjava3.core.Observable

interface ServerTestStub {

    fun connectClientToServer(connectionInitiator: PlayerHostTest)

    fun guestSendsMessageToServer(sender: PlayerHostTest, message: Message, enableThreadBreak: Boolean = false)

    fun observeMessagesSent(): Observable<String>

    fun observeMessagesBroadcasted(): Observable<String>

    fun disconnectFromServer(guestIdentifier: PlayerHostTest, enableThreadBreak: Boolean = false)
}
