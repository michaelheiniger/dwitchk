package ch.qscqlmpa.dwitchengine.actions.cardexchange

import ch.qscqlmpa.dwitchengine.actions.GameUpdaterBase
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId

internal class CardExchangeChooserGameUpdater(currentGameState: DwitchGameState) : GameUpdaterBase(currentGameState) {

    fun addCardsForExchange(playerId: DwitchPlayerId, cardsForExchange: Set<Card>) {
        gameStateMutable.addCardsForExchange(playerId, cardsForExchange)
    }
}
