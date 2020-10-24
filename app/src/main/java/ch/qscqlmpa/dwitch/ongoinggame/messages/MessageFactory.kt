package ch.qscqlmpa.dwitch.ongoinggame.messages

import ch.qscqlmpa.dwitch.ongoinggame.communication.RecipientType
import ch.qscqlmpa.dwitchengine.model.game.GameState

object MessageFactory {

    fun createGameStateUpdatedMessage(gameState: GameState): EnvelopeToSend {
        return EnvelopeToSend(RecipientType.All, Message.GameStateUpdatedMessage(gameState))
    }

    fun createGameOverMessage(): EnvelopeToSend {
        return EnvelopeToSend(RecipientType.All, Message.GameOverMessage)
    }
}