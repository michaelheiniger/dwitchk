package ch.qscqlmpa.dwitchcommunication.ingame.websocket.server.test

import ch.qscqlmpa.dwitchcommunication.ingame.model.Message

interface ServerTestStub {

    fun connectClientToServer(connectionInitiator: PlayerHostTest)

    fun clientSendsMessageToServer(sender: PlayerHostTest, message: Message)

    fun blockUntilMessageSentIsAvailable(): String

    fun clientDisconnectsFromServer(guestIdentifier: PlayerHostTest)
}
