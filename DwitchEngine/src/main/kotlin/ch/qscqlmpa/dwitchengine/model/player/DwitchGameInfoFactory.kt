package ch.qscqlmpa.dwitchengine.model.player

import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.game.DwitchGamePhase
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.info.DwitchCardInfo
import ch.qscqlmpa.dwitchengine.model.info.DwitchGameInfo
import ch.qscqlmpa.dwitchengine.model.info.DwitchPlayerInfo

internal class DwitchGameInfoFactory(val gameState: DwitchGameState) {

    private val minimumCardValueAllowed: Int by lazy {
        gameState.lastCardsPlayed()?.name?.value ?: 0
    }

    fun create(): DwitchGameInfo {
        return DwitchGameInfo(
            gameState.currentPlayerId,
            playerInfos(),
            gameState.phase,
            gameState.playingOrder,
            gameState.joker,
            gameState.lastCardsPlayed(),
            gameState.cardsOnTable,
            gameState.lastPlayerAction,
            roundIsOver()
        )
    }

    private fun playerInfos(): Map<DwitchPlayerId, DwitchPlayerInfo> {
        return gameState.players.entries.associate { entry -> entry.key to playerInfo(entry.value) }
    }

    private fun playerInfo(player: DwitchPlayer): DwitchPlayerInfo {
        return DwitchPlayerInfo(
            player.id,
            player.name,
            player.rank,
            player.status,
            player.dwitched,
            buildCardInHands(player),
            canPlay(player)
        )
    }

    private fun buildCardInHands(player: DwitchPlayer): List<DwitchCardInfo> {
        val cardsMultiplicity = player.cardsInHand.groupBy { c -> c.name }.mapValues { (_, l) -> l.size }
        return player.cardsInHand.map { card ->
            val cardMultiplicity = cardsMultiplicity[card.name] ?: 0
            DwitchCardInfo(card, isCardPlayable(card.name, cardMultiplicity))
        }
    }

    private fun isCardPlayable(cardName: CardName, cardNameMultiplicity: Int): Boolean {
        val lastCardPlayedMultiplicity = gameState.lastCardsPlayed()?.multiplicity
        if (lastCardPlayedMultiplicity != null) { // Number of cards to play is constrained
            return cardNameMultiplicity >= lastCardPlayedMultiplicity && cardHasHighEnoughValue(cardName)
        }
        // Number of cards to play can be between 1 and 4
        return cardHasHighEnoughValue(cardName)
    }

    private fun cardHasHighEnoughValue(cardName: CardName): Boolean {
        return cardName.value >= minimumCardValueAllowed || cardName == gameState.joker
    }

    private fun canPlay(player: DwitchPlayer) = gameState.phaseIsPlayable && player.isTheOnePlaying

    private fun roundIsOver() = gameState.phase == DwitchGamePhase.RoundIsOver
}
