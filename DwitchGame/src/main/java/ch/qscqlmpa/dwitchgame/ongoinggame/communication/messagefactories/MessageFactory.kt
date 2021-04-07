package ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories

import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchstore.ingamestore.InGameStore
import javax.inject.Inject

class MessageFactory @Inject constructor(private val store: InGameStore) {

    fun createGameStateUpdatedMessage(): Message {
        return Message.GameStateUpdatedMessage(store.getGameState())
    }

    companion object {

        fun createGameStateUpdatedMessage(gameState: DwitchGameState): Message {
            return Message.GameStateUpdatedMessage(gameState)
        }

        fun createCardsForExchangeChosenMessage(playerId: DwitchPlayerId, cards: Set<Card>): Message {
            return Message.CardsForExchangeMessage(playerId, cards)
        }
    }
}
