package ch.qscqlmpa.dwitchengine.actions.cardexchange

import ch.qscqlmpa.dwitchengine.actions.GameUpdaterBase
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId

internal class AddCardsForExchangeGameUpdater(currentGameState: GameState) : GameUpdaterBase(currentGameState) {

    fun addCardsForExchange(playerId: PlayerInGameId, cardsForExchange: Set<Card>) {
        gameStateMutable.addCardsForExchange(playerId, cardsForExchange)
    }
}