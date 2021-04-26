package ch.qscqlmpa.dwitchengine.model.player

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.game.DwitchGamePhase
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.info.DwitchCardInfo
import ch.qscqlmpa.dwitchengine.model.info.DwitchGameInfo
import ch.qscqlmpa.dwitchengine.model.info.DwitchPlayerInfo

internal class DwitchGameInfoFactory(val gameState: DwitchGameState) {

    private val minimumCardValueAllowed: CardName by lazy {
        val lastCardOnTable = gameState.lastCardOnTable()
        lastCardOnTable?.name ?: CardName.Blank
    }

    fun create(): DwitchGameInfo {
        return DwitchGameInfo(
            gameState.currentPlayerId,
            playerInfos(),
            gameState.phase,
            gameState.playingOrder,
            gameState.joker,
            lastCardPlayed(),
            gameState.cardsOnTable,
            gameState.dwitchGameEvent
        )
    }

    private fun playerInfos(): Map<DwitchPlayerId, DwitchPlayerInfo> {
        return gameState.players.entries.map { entry -> entry.key to playerInfo(entry.value) }.toMap()
    }

    private fun playerInfo(player: DwitchPlayer): DwitchPlayerInfo {
        val canStartNewRound = roundIsOver()
        return DwitchPlayerInfo(
            player.id,
            player.name,
            player.rank,
            player.status,
            player.dwitched,
            player.cardsInHand.map { card -> DwitchCardInfo(card, isCardPlayable(card)) },
            canPlay(player),
            canStartNewRound
        )
    }

    private fun isCardPlayable(card: Card) = cardHasValueHighEnough(card) || cardIsJoker(card)

    private fun cardHasValueHighEnough(card: Card) = card.value() >= minimumCardValueAllowed.value || cardIsJoker(card)

    private fun canPlay(player: DwitchPlayer) = gameState.phaseIsPlayable && player.isTheOnePlaying

    private fun cardIsJoker(card: Card) = card.name == gameState.joker

    private fun roundIsOver() = gameState.phase == DwitchGamePhase.RoundIsOver

    private fun lastCardPlayed() = gameState.lastCardOnTable() ?: Card.Blank
}
