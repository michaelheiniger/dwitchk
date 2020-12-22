package ch.qscqlmpa.dwitchengine.model.player

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.game.GamePhase
import ch.qscqlmpa.dwitchengine.model.game.GameState

object PlayerDashboardFactory {

    fun create(gameState: GameState, playerId: PlayerInGameId): PlayerDashboard {
        val player = gameState.player(playerId)

        val lastCardPlayed = gameState.lastCardOnTable() ?: Card.Blank

        return PlayerDashboard(
            player,
            lastCardPlayed,
            gameState.joker,
            gameState.phase,
            players(gameState),
            gameState.playingOrder,
            player.cardsInHand,
            canPass(gameState, player),
            canPickACard(gameState, player),
            canPlay(gameState, player),
            canStartNewRound(gameState),
            canEndGame(gameState),
            gameState.cardsOnTable,
            minimumCardValueAllowed(gameState),
            gameState.gameEvent
        )
    }

    private fun players(gameState: GameState): Map<PlayerInGameId, Player> {
        return gameState.playingOrder.map { id -> id to gameState.player(id) }.toMap()
    }

    private fun canPlay(gameState: GameState, player: Player): Boolean {
        return gameState.phaseIsPlayable && player.isTheOnePlaying
    }

    private fun canPickACard(gameState: GameState, player: Player): Boolean {
        return gameState.phaseIsPlayable && player.isTheOnePlaying && player.hasNotPickedACard
    }

    private fun canPass(gameState: GameState, player: Player): Boolean {
        return gameState.phaseIsPlayable && player.isTheOnePlaying && player.hasPickedACard
    }

    private fun canStartNewRound(gameState: GameState): Boolean {
        return roundIsOver(gameState)
    }

    private fun canEndGame(gameState: GameState): Boolean {
        return roundIsOver(gameState)
    }

    private fun minimumCardValueAllowed(gameState: GameState): CardName {
        val lastCardOnTable = gameState.lastCardOnTable()
        return lastCardOnTable?.name ?: CardName.Blank
    }

    private fun roundIsOver(gameState: GameState): Boolean {
        return gameState.phase == GamePhase.RoundIsOver
    }
}