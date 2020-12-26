package ch.qscqlmpa.dwitchengine.model.player

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.game.GamePhase
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.info.GameInfo
import ch.qscqlmpa.dwitchengine.model.info.PlayerInfo

class PlayerDashboardFactory(val gameState: GameState) {

    fun create(): GameInfo {
        return GameInfo(
            playerInfos(),
            gameState.phase,
            gameState.playingOrder,
            gameState.joker,
            lastCardPlayed(),
            gameState.cardsOnTable,
            gameState.gameEvent
        )
    }

    private fun playerInfos(): Map<PlayerInGameId, PlayerInfo> {
        return gameState.players.entries.map { entry -> entry.key to playerInfo(entry.value) }.toMap()
    }

    private fun playerInfo(player: Player): PlayerInfo {
        return PlayerInfo(
            player.id,
            player.name,
            player.rank,
            player.status,
            player.dwitched,
            player.cardsInHand,
            canPass(player),
            canPickACard(player),
            canPlay(player),
            canStartNewRound(),
            canEndGame(),
            minimumCardValueAllowed()
        )
    }

    private fun canPlay(player: Player): Boolean {
        return gameState.phaseIsPlayable && player.isTheOnePlaying
    }

    private fun canPickACard(player: Player): Boolean {
        return gameState.phaseIsPlayable && player.isTheOnePlaying && player.hasNotPickedACard
    }

    private fun canPass(player: Player): Boolean {
        return gameState.phaseIsPlayable && player.isTheOnePlaying && player.hasPickedACard
    }

    private fun canStartNewRound(): Boolean {
        return roundIsOver()
    }

    private fun canEndGame(): Boolean {
        return roundIsOver()
    }

    private fun minimumCardValueAllowed(): CardName {
        val lastCardOnTable = gameState.lastCardOnTable()
        return lastCardOnTable?.name ?: CardName.Blank
    }

    private fun roundIsOver(): Boolean {
        return gameState.phase == GamePhase.RoundIsOver
    }

    private fun lastCardPlayed(): Card {
        return gameState.lastCardOnTable() ?: Card.Blank
    }
}