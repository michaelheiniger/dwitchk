package ch.qscqlmpa.dwitchengine.actions.cardexchange

import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState

internal class CardExchangeChooser(
    private val gameState: CardExchangeChooserState,
    private val gameUpdater: CardExchangeChooserGameUpdater
) {

    fun getUpdatedGameState(): DwitchGameState {
        gameState.checkState()
        gameUpdater.addCardsForExchange(gameState.playerId(), gameState.cardsForExchange())
        return gameUpdater.buildUpdatedGameState()
    }
}
