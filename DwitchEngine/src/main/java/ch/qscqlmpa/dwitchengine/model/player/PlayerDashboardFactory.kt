package ch.qscqlmpa.dwitchengine.model.player

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.game.GamePhase
import ch.qscqlmpa.dwitchengine.model.game.GameState
import ch.qscqlmpa.dwitchengine.model.info.CardItem
import ch.qscqlmpa.dwitchengine.model.info.GameInfo
import ch.qscqlmpa.dwitchengine.model.info.PlayerInfo

class PlayerDashboardFactory(val gameState: GameState) {

    private val minimumCardValueAllowed: CardName by lazy {
        val lastCardOnTable = gameState.lastCardOnTable()
        lastCardOnTable?.name ?: CardName.Blank
    }

    fun create(): GameInfo {
        return GameInfo(
            gameState.currentPlayerId,
            playerInfos(),
            gameState.phase,
            gameState.playingOrder,
            gameState.joker,
            lastCardPlayed(),
            gameState.cardsOnTable,
            gameState.gameEvent
        )
    }

    private fun playerInfos(): Map<PlayerDwitchId, PlayerInfo> {
        return gameState.players.entries.map { entry -> entry.key to playerInfo(entry.value) }.toMap()
    }

    private fun playerInfo(player: Player): PlayerInfo {
        return PlayerInfo(
            player.id,
            player.name,
            player.rank,
            player.status,
            player.dwitched,
            player.cardsInHand.map { card -> CardItem(card, isCardPlayable(card)) },
            canPass(player),
            canPickACard(player),
            canPlay(player),
            canStartNewRound()
        )
    }

    private fun isCardPlayable(card: Card) = cardHasValueHighEnough(card) || cardIsJoker(card)

    private fun cardHasValueHighEnough(card: Card) =
        card.value() >= minimumCardValueAllowed.value || cardIsJoker(card)

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

    private fun cardIsJoker(card: Card) = card.name == gameState.joker

    private fun roundIsOver(): Boolean {
        return gameState.phase == GamePhase.RoundIsOver
    }

    private fun lastCardPlayed(): Card {
        return gameState.lastCardOnTable() ?: Card.Blank
    }
}
