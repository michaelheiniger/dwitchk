package ch.qscqlmpa.dwitchcommunication.websocket.client.test

import ch.qscqlmpa.dwitchcommunication.model.Message
import io.reactivex.rxjava3.core.Observable

interface ClientTestStub {

    fun setConnectToServerOutcome(event: OnStartEvent)

    fun serverSendsMessageToClient(message: Message)

    fun breakConnectionWithHost()

    /**
     * @return last message sent, if any.
     */
    fun observeMessagesSent(): Observable<String>
}
