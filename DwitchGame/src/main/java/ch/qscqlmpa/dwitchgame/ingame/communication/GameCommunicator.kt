package ch.qscqlmpa.dwitchgame.ingame.communication

import ch.qscqlmpa.dwitchcommunication.model.Message

internal interface GameCommunicator {

    fun sendMessageToHost(message: Message)
}