package ch.qscqlmpa.dwitchengine.model.player

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.game.GamePhase
import ch.qscqlmpa.dwitchengine.model.game.GameState

object PlayerDashboardFactory {

    fun create(gameState: GameState): PlayerDashboard {

        val localPlayer = gameState.localPlayer()

        val lastCardPlayed = gameState.lastCardOnTable() ?: Card.Blank

        return PlayerDashboard(
                localPlayer,
                lastCardPlayed,
                gameState.joker,
                gameState.phase,
                players(gameState),
                gameState.playingOrder,
                localPlayer.cardsInHand,
                canPass(gameState),
                canPickACard(gameState),
                canPlay(gameState),
                canStartNewRound(gameState),
                canEndGame(gameState),
                gameState.cardsOnTable,
                minimumCardValueAllowed(gameState),
                gameState.gameEvent
        )
    }

    private fun players(gameState: GameState): Map<PlayerInGameId, Player> {
        return gameState.playingOrder
                .map { id -> id to gameState.player(id) }
                .toMap()
    }

    private fun canPlay(gameState: GameState): Boolean {
        val localPlayer = gameState.localPlayer()
        return localPlayer == gameState.currentPlayer() && roundIsNotOver(gameState)
    }

    private fun canPickACard(gameState: GameState): Boolean {
        val localPlayer = gameState.localPlayer()
        return if (localPlayer == gameState.currentPlayer()) {
            localPlayer.state == PlayerState.Playing
                    && !localPlayer.hasPickedCard
                    && roundIsNotOver(gameState)
        } else {
            false
        }
    }

    private fun canPass(gameState: GameState): Boolean {
        val localPlayer = gameState.localPlayer()
        return if (localPlayer == gameState.currentPlayer()) {
            localPlayer.hasPickedCard && roundIsNotOver(gameState)
        } else {
            false
        }
    }

    private fun canStartNewRound(gameState: GameState): Boolean {
        return roundIsOver(gameState)
    }

    private fun canEndGame(gameState: GameState): Boolean {
        return roundIsOver(gameState)
    }

    private fun minimumCardValueAllowed(gameState: GameState): CardName {
        val lastCardOnTable = gameState.lastCardOnTable()
        return lastCardOnTable?.name ?: CardName.Two
    }

    private fun roundIsNotOver(gameState: GameState): Boolean {
        return !roundIsOver(gameState)
    }

    private fun roundIsOver(gameState: GameState): Boolean {
        return gameState.phase == GamePhase.RoundIsOver
    }
}