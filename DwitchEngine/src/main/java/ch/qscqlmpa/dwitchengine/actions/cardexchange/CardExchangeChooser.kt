package ch.qscqlmpa.dwitchengine.actions.cardexchange

import ch.qscqlmpa.dwitchengine.model.game.GameState

internal class CardExchangeChooser(
    private val gameState: CardExchangeChooserState,
    private val gameUpdater: CardExchangeChooserGameUpdater
) {

    fun getUpdatedGameState(): GameState {
        gameState.checkState()
        gameUpdater.addCardsForExchange(gameState.playerId(), gameState.cardsForExchange())
        return gameUpdater.buildUpdatedGameState()
    }
}
