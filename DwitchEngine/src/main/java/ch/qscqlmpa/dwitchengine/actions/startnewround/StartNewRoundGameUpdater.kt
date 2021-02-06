package ch.qscqlmpa.dwitchengine.actions.startnewround

import ch.qscqlmpa.dwitchengine.actions.GameUpdaterBase
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId

internal class StartNewRoundGameUpdater(currentGameState: GameState) : GameUpdaterBase(currentGameState) {

    fun clearTable() {
        gameStateMutable.clearTable()
    }

    fun clearGraveyard() {
        gameStateMutable.clearGraveyard()
    }

    fun joker(cardName: CardName) {
        gameStateMutable.joker = cardName
    }

    fun cardsInDeck(cards: List<Card>) {
        gameStateMutable.cardsInDeck.clear()
        gameStateMutable.cardsInDeck.addAll(cards)
    }

    fun cardsInHandOfPlayer(playerId: PlayerDwitchId, cards: List<Card>) {
        gameStateMutable.players.getValue(playerId).cardsInHand(cards)
    }

    fun playingOrder(playingOrder: List<PlayerDwitchId>) {
        gameStateMutable.playingOrder.clear()
        gameStateMutable.playingOrder.addAll(playingOrder)
    }

    fun clearDonePlayersInFinishingOrder() {
        gameStateMutable.playersDoneForRound.clear()
    }

    fun activePlayers(players: List<PlayerDwitchId>) {
        gameStateMutable.activePlayers.clear()
        gameStateMutable.activePlayers.addAll(players)
    }

    fun setFirstCardOnTable(card: Card) {
        gameStateMutable.setCardsOnTable(card)
    }
}
