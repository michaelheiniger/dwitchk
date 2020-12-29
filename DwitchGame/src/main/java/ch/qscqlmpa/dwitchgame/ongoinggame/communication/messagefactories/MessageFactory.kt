package ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories

import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId

object MessageFactory {

    fun createGameStateUpdatedMessage(gameState: GameState): Message {
        return Message.GameStateUpdatedMessage(gameState)
    }

    fun createCardsForExchangeChosenMessage(playerId: PlayerDwitchId, cards: Set<Card>): Message {
        return Message.CardsForExchangeMessage(playerId, cards)
    }
}