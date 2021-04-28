package ch.qscqlmpa.dwitchgame.ongoinggame.communication

import ch.qscqlmpa.dwitchcommunication.model.Message

internal interface GameCommunicator {

    fun sendMessageToHost(message: Message)
}
