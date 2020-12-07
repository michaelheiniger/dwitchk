package ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories

import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchengine.model.game.GameState

object MessageFactory {

    fun createGameStateUpdatedMessage(gameState: GameState): Message {
        return Message.GameStateUpdatedMessage(gameState)
    }

    fun createCardsForExchangeChoseMessage() {
        //TODO: The host is also a player that might have to choose cards for the exchange.
        //TODO: Should it send a message to itself ? only to itself ?
        return EnvelopeToSend(RecipientType.SingleGuest)
    }
}