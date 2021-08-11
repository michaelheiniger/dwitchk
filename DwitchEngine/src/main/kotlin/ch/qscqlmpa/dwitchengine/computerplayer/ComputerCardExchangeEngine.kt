package ch.qscqlmpa.dwitchengine.computerplayer

import ch.qscqlmpa.dwitchengine.DwitchEngine
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardValueAscComparator
import ch.qscqlmpa.dwitchengine.model.card.CardValueDescComparator
import ch.qscqlmpa.dwitchengine.model.game.DwitchCardExchange
import ch.qscqlmpa.dwitchengine.model.info.DwitchCardInfo
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank

internal class ComputerCardExchangeEngine(
    private val dwitchEngine: DwitchEngine,
    private val computerPlayersId: Set<DwitchPlayerId>
) {

    private val gameInfo = dwitchEngine.getGameInfo()

    fun performCardExchangeIfNeeded(): List<ComputerPlayerActionResult> {
        return computerPlayersId.mapNotNull { playerId ->
            val cardExchange = dwitchEngine.getCardExchangeIfRequired(playerId) ?: return@mapNotNull null

            val player = gameInfo.playerInfos.getValue(playerId)
            val cardsInHand = cardsInHand(playerId).map(DwitchCardInfo::card)
            val cardsForExchange = when (player.rank) {
                DwitchRank.ViceAsshole, DwitchRank.Asshole -> chooseCardsWithHighestValues(cardExchange, cardsInHand)
                DwitchRank.VicePresident, DwitchRank.President -> chooseCardsWithLowestValues(cardExchange, cardsInHand)
                else -> throw IllegalStateException("Neutral players don't take part in card exchange.")
            }
            val updatedGameState = dwitchEngine.chooseCardsForExchange(playerId, cardsForExchange)
            ComputerPlayerActionResult(playerId, updatedGameState)
        }
    }

    private fun chooseCardsWithHighestValues(cardExchange: DwitchCardExchange, cards: List<Card>): Set<Card> {
        val remainingAllowedCardValues = cardExchange.allowedCardValues.toMutableList()
        val cardsSortedAsc = cards
            .filter { c -> remainingAllowedCardValues.remove(c.name) }
            .sortedWith(CardValueDescComparator())
        return (1..cardExchange.numCardsToChoose).map { i -> cardsSortedAsc[i - 1] }.toSet()
    }

    private fun chooseCardsWithLowestValues(cardExchange: DwitchCardExchange, cards: List<Card>): Set<Card> {
        val cardsSortedDesc = cards.sortedWith(CardValueAscComparator())
        return (1..cardExchange.numCardsToChoose).map { i -> cardsSortedDesc[i - 1] }.toSet()
    }

    private fun cardsInHand(playerId: DwitchPlayerId): List<DwitchCardInfo> {
        return gameInfo.playerInfos.getValue(playerId).cardsInHand
    }
}
