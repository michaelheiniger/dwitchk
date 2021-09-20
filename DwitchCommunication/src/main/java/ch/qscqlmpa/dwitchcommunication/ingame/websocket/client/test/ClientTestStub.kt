package ch.qscqlmpa.dwitchcommunication.ingame.websocket.client.test

import ch.qscqlmpa.dwitchcommunication.ingame.model.Message

interface ClientTestStub {

    fun connectClientToServer(event: OnStartEvent)

    fun serverSendsMessageToClient(message: Message)

    fun serverClosesConnectionWithClient()

    fun breakConnectionWithServer()

    /**
     * @return last message sent, if any.
     */
    fun blockUntilMessageSentIsAvailable(): String
}
