package ch.qscqlmpa.dwitchengine.actions.cardexchange

import ch.qscqlmpa.dwitchengine.actions.GameUpdaterBase
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId

internal class CardExchangePerformerGameUpdater(currentGameState: DwitchGameState) : GameUpdaterBase(currentGameState) {

    fun performCardExchange(
        player1: DwitchPlayerId,
        player2: DwitchPlayerId,
        cardsGivenByPlayer1: Set<Card>,
        cardsGivenByPlayer2: Set<Card>
    ) {
        gameStateMutable.removeAllCardsForCardExchange(player2)
        gameStateMutable.addCardsToHand(player1, cardsGivenByPlayer2.toList())

        gameStateMutable.removeAllCardsForCardExchange(player1)
        gameStateMutable.addCardsToHand(player2, cardsGivenByPlayer1.toList())
    }
}
