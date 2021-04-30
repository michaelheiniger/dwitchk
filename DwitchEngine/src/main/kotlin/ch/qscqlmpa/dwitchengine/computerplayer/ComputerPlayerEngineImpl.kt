package ch.qscqlmpa.dwitchengine.computerplayer

import ch.qscqlmpa.dwitchengine.DwitchEngine
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardValueAscComparator
import ch.qscqlmpa.dwitchengine.model.card.CardValueDescComparator
import ch.qscqlmpa.dwitchengine.model.card.DwitchCardInfoValueAscComparator
import ch.qscqlmpa.dwitchengine.model.game.DwitchCardExchange
import ch.qscqlmpa.dwitchengine.model.game.DwitchGamePhase
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.info.DwitchCardInfo
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank
import org.tinylog.kotlin.Logger

internal class ComputerPlayerEngineImpl(
    private val dwitchEngine: DwitchEngine,
    private val computerPlayersId: Set<DwitchPlayerId>
) : DwitchComputerPlayerEngine {

    private val gameInfo = dwitchEngine.getGameInfo()

    override fun handleComputerPlayerAction(): List<ComputerPlayerActionResult> {
        return when (dwitchEngine.getGameInfo().gamePhase) {
            DwitchGamePhase.RoundIsBeginning,
            DwitchGamePhase.RoundIsOnGoing -> playIfNeeded()
            DwitchGamePhase.CardExchange -> performCardExchangeIfNeeded()
            else -> emptyList() // Nothing to do
        }
    }

    private fun playIfNeeded(): List<ComputerPlayerActionResult> {
        val playingPlayerId = computerPlayersId.find { id -> id == gameInfo.currentPlayerId }
            ?: return emptyList() // It is not the turn of a computer player
        return listOf(playOrPass(playingPlayerId))
    }

    private fun playOrPass(playerId: DwitchPlayerId): ComputerPlayerActionResult {
        val updatedGameState = when {
            // Don't want to break the "First Jack of the round" special rule, so the player passes
            dwitchEngine.isLastCardPlayedTheFirstJackOfTheRound() -> passTurn(playerId)

            // Play joker to prevent breaking the "finish with joker" special rule
            playerHasOnlyOneNonJokerCard(playerId) -> playJoker(playerId)

            // Heuristic: play the cards with smallest values first
            else -> playCardWithSmallestValueOrPassTurn(playerId)
        }
        return ComputerPlayerActionResult(playerId, updatedGameState)
    }

    private fun passTurn(playerId: DwitchPlayerId): DwitchGameState {
        Logger.debug { "Computer player with id $playerId passes its turn." }
        return dwitchEngine.passTurn()
    }

    private fun playerHasOnlyOneNonJokerCard(playerId: DwitchPlayerId): Boolean {
        val (jokers, others) = cardsInHand(playerId).partition { c -> c.card.name == gameInfo.joker }
        return jokers.isNotEmpty() && others.size == 1
    }

    private fun playJoker(playerId: DwitchPlayerId): DwitchGameState {
        val cardToPlay = cardsInHand(playerId).find { c -> c.card.name == dwitchEngine.joker() }!!
        Logger.debug { "Computer player with id $playerId plays joker $cardToPlay to avoid breaking special rule." }
        return dwitchEngine.playCard(cardToPlay.card)
    }

    private fun playCardWithSmallestValueOrPassTurn(playerId: DwitchPlayerId): DwitchGameState {
        val cardsSortedAsc = cardsInHand(playerId).sortedWith(DwitchCardInfoValueAscComparator())
        val cardToPlay = cardsSortedAsc.find { c -> c.selectable }?.card
        return if (cardToPlay != null) {
            Logger.debug { "Computer player with id $playerId plays card $cardToPlay." }
            dwitchEngine.playCard(cardToPlay)
        } else {
            passTurn(playerId)
        }
    }

    private fun performCardExchangeIfNeeded(): List<ComputerPlayerActionResult> {
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