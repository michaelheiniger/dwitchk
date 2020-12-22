package ch.qscqlmpa.dwitchengine.actions.cardexchange

import ch.qscqlmpa.dwitchengine.actions.GameUpdaterBase
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId

internal class CardExchangeGameUpdater(currentGameState: GameState) : GameUpdaterBase(currentGameState) {

    fun performCardExchange(
        player1: PlayerInGameId,
        player2: PlayerInGameId,
        cardsGivenByPlayer1: Set<Card>,
        cardsGivenByPlayer2: Set<Card>
    ) {
        gameStateMutable.removeAllCardsForCardExchange(player2)
        gameStateMutable.addCardsToHand(player1, cardsGivenByPlayer2.toList())

        gameStateMutable.removeAllCardsForCardExchange(player1)
        gameStateMutable.addCardsToHand(player2, cardsGivenByPlayer1.toList())
    }
}