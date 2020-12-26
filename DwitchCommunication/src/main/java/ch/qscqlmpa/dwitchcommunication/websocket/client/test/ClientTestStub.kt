package ch.qscqlmpa.dwitchcommunication.websocket.client.test

import ch.qscqlmpa.dwitchcommunication.model.Message
import io.reactivex.rxjava3.core.Observable

interface ClientTestStub {

    fun connectClientToServer(enableThreadBreak: Boolean)

    fun serverSendsMessageToClient(message: Message, enableThreadBreak: Boolean)

    fun breakConnectionWithHost()

    /**
     * @return last message sent, if any.
     */
    fun observeMessagesSent(): Observable<String>
}