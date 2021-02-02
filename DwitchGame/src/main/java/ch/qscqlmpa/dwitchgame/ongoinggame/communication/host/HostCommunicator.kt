package ch.qscqlmpa.dwitchgame.ongoinggame.communication.host

import ch.qscqlmpa.dwitchcommunication.connectionstore.ConnectionId
import ch.qscqlmpa.dwitchcommunication.model.EnvelopeToSend
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.GameCommunicator

internal interface HostCommunicator : GameCommunicator {

    fun sendMessage(envelopeToSend: EnvelopeToSend)

    fun startServer()

    fun stopServer()

    fun closeConnectionWithClient(connectionId: ConnectionId)
}