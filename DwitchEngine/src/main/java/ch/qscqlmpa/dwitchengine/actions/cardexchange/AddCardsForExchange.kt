package ch.qscqlmpa.dwitchengine.actions.cardexchange

import ch.qscqlmpa.dwitchengine.model.game.GameState

internal class AddCardsForExchange(
    private val addCardsForExchangeState: AddCardsForExchangeState,
    private val gameUpdater: AddCardsForExchangeGameUpdater
) {

    fun getUpdatedGameState(): GameState {
        addCardsForExchangeState.checkState()
        gameUpdater.addCardsForExchange(addCardsForExchangeState.playerId(), addCardsForExchangeState.cardsForExchange())
        return gameUpdater.buildUpdatedGameState()
    }
}