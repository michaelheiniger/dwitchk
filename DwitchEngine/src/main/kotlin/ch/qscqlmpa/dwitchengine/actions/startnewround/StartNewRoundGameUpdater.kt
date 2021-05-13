package ch.qscqlmpa.dwitchengine.actions.startnewround

import ch.qscqlmpa.dwitchengine.actions.GameUpdaterBase
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId

internal class StartNewRoundGameUpdater(currentGameState: DwitchGameState) : GameUpdaterBase(currentGameState) {

    fun clearTable() {
        gameStateMutable.clearTable()
    }

    fun clearGraveyard() {
        gameStateMutable.clearGraveyard()
    }

    fun joker(cardName: CardName) {
        gameStateMutable.joker = cardName
    }

    fun cardsInDeck(cards: Set<Card>) {
        gameStateMutable.cardsInDeck.clear()
        gameStateMutable.cardsInDeck.addAll(cards)
    }

    fun cardsInHandOfPlayer(playerId: DwitchPlayerId, cards: Set<Card>) {
        gameStateMutable.players.getValue(playerId).cardsInHand(cards)
    }

    fun playingOrder(playingOrder: List<DwitchPlayerId>) {
        gameStateMutable.playingOrder.clear()
        gameStateMutable.playingOrder.addAll(playingOrder)
    }

    fun clearDonePlayersInFinishingOrder() {
        gameStateMutable.playersDoneForRound.clear()
    }

    fun activePlayers(players: List<DwitchPlayerId>) {
        gameStateMutable.activePlayers.clear()
        gameStateMutable.activePlayers.addAll(players)
    }
}
