package ch.qscqlmpa.dwitch.ongoinggame.messages

import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitch.ongoinggame.communication.RecipientType
import ch.qscqlmpa.dwitch.ongoinggame.messages.EnvelopeToSend
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message

object MessageFactory {

    fun createGameStateUpdatedMessage(gameState: GameState): EnvelopeToSend {
        return EnvelopeToSend(RecipientType.All, Message.GameStateUpdatedMessage(gameState))
    }
}