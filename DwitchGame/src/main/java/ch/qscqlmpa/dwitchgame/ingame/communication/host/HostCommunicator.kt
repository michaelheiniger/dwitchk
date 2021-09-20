package ch.qscqlmpa.dwitchgame.ingame.communication.host

import ch.qscqlmpa.dwitchcommunication.ingame.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.ingame.model.EnvelopeToSend
import ch.qscqlmpa.dwitchgame.ingame.communication.GameCommunicator

internal interface HostCommunicator : GameCommunicator {

    fun startServer()

    fun stopServer()

    fun closeConnectionWithClient(connectionId: ConnectionId)

    fun sendMessage(envelopeToSend: EnvelopeToSend)
}
