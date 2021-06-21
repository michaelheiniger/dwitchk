package ch.qscqlmpa.dwitchcommunication.websocket.server.test

import ch.qscqlmpa.dwitchcommunication.model.Message

interface ServerTestStub {

    fun connectClientToServer(connectionInitiator: PlayerHostTest)

    fun clientSendsMessageToServer(sender: PlayerHostTest, message: Message)

    fun blockUntilMessageSentIsAvailable(): String

    fun clientDisconnectsFromServer(guestIdentifier: PlayerHostTest)
}
