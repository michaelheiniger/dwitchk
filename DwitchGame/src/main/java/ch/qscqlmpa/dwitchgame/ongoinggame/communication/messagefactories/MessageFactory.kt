package ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories

import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchengine.model.game.GameState

object MessageFactory {

    fun createGameStateUpdatedMessage(gameState: GameState): Message {
        return Message.GameStateUpdatedMessage(gameState)
    }
}