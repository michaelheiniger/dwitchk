package ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories

import ch.qscqlmpa.dwitchcommunication.model.EnvelopeToSend
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.model.RecipientType
import ch.qscqlmpa.dwitchengine.model.game.GameState

object MessageFactory {

    fun createGameStateUpdatedMessage(gameState: GameState): EnvelopeToSend {
        return EnvelopeToSend(RecipientType.All, Message.GameStateUpdatedMessage(gameState))
    }

    fun createGameOverMessage(): EnvelopeToSend {
        return EnvelopeToSend(RecipientType.All, Message.GameOverMessage)
    }

    fun createCardsForExchangeChoseMessage() {
        //TODO: The host is also a player that might have to choose cards for the exchange.
        //TODO: Should it send a message to itself ? only to itself ?
        return EnvelopeToSend(RecipientType.SingleGuest)
    }
}