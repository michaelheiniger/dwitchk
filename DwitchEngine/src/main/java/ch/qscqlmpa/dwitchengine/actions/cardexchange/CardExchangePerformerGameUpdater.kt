package ch.qscqlmpa.dwitchengine.actions.cardexchange

import ch.qscqlmpa.dwitchengine.actions.GameUpdaterBase
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId

internal class CardExchangePerformerGameUpdater(currentGameState: GameState) : GameUpdaterBase(currentGameState) {

    fun performCardExchange(
        player1: PlayerDwitchId,
        player2: PlayerDwitchId,
        cardsGivenByPlayer1: Set<Card>,
        cardsGivenByPlayer2: Set<Card>
    ) {
        gameStateMutable.removeAllCardsForCardExchange(player2)
        gameStateMutable.addCardsToHand(player1, cardsGivenByPlayer2.toList())

        gameStateMutable.removeAllCardsForCardExchange(player1)
        gameStateMutable.addCardsToHand(player2, cardsGivenByPlayer1.toList())
    }
}