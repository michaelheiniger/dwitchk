package ch.qscqlmpa.dwitchcommunication.websocket.client.test

import ch.qscqlmpa.dwitchcommunication.model.Message

interface ClientTestStub {

    fun connectClientToServer(event: OnStartEvent)

    fun serverSendsMessageToClient(message: Message)

    fun breakConnectionWithServer()

    /**
     * @return last message sent, if any.
     */
    fun blockUntilMessageSentIsAvailable(): String
}
