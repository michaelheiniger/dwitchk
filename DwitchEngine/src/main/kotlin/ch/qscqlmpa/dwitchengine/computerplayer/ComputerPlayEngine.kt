package ch.qscqlmpa.dwitchengine.computerplayer

import ch.qscqlmpa.dwitchengine.DwitchEngine
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.card.CardNameValueAscComparator
import ch.qscqlmpa.dwitchengine.model.game.DwitchGameState
import ch.qscqlmpa.dwitchengine.model.game.PlayedCards
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import org.tinylog.kotlin.Logger

internal class ComputerPlayEngine(
    private val dwitchEngine: DwitchEngine,
    private val playingPlayerId: DwitchPlayerId
) {

    private val gameInfo = dwitchEngine.getGameInfo()
    private val lastPlayedCards = gameInfo.lastCardPlayed
    private val cardsInHand = gameInfo.playerInfos.getValue(playingPlayerId).cardsInHand
    private val joker = gameInfo.joker

    private val cardsMultiplicity = computeCardsMultiplicity()
    private val selectableCardsMultiplicity = computeSelectableCardsMultiplicity()

    fun play(): ComputerPlayerActionResult {
        val updatedGameState = when {
            // Don't want to break the "First Jack of the round" special rule, so the player passes
            dwitchEngine.isLastCardPlayedTheFirstJackOfTheRound() -> passTurn()

            // Play joker to prevent breaking the "finish with joker" special rule
            onlyOneNonJokerCardToPlayAndJokerCanBePlayed() -> playAsManyJokersAsPossible()

            // Pass to prevent breaking the "finish with joker" special rule
            onlyOneNonJokerCardToPlayAndJokerCannotBePlayedNow() -> passTurn()

            // Heuristic: play the cards with smallest values first
            else -> playCardsOrPassTurn()
        }
        return ComputerPlayerActionResult(playingPlayerId, updatedGameState)
    }

    private fun passTurn(): DwitchGameState {
        Logger.debug { "Computer player with id $playingPlayerId passes its turn." }
        return dwitchEngine.passTurn()
    }

    private fun onlyOneNonJokerCardToPlayAndJokerCanBePlayed(): Boolean {
        val numJokersInHand = cardsMultiplicity[joker] ?: 0
        val numNonJokersCardInHand = cardsMultiplicity.filterKeys { name -> name != joker }.size
        return if (lastPlayedCards == null) {
            numJokersInHand > 0 && numNonJokersCardInHand == 1
        } else {
            val numCardsToPlay = lastPlayedCards.multiplicity
            numJokersInHand == numCardsToPlay && numNonJokersCardInHand == 1
        }
    }

    private fun onlyOneNonJokerCardToPlayAndJokerCannotBePlayedNow(): Boolean {
        return if (lastPlayedCards != null) {
            val numCardsToPlay = lastPlayedCards.multiplicity
            val numJokersInHand = cardsMultiplicity[joker] ?: 0
            val numNonJokersCardInHand = cardsMultiplicity.filterKeys { name -> name != joker }.size
            numJokersInHand != numCardsToPlay && numNonJokersCardInHand == 1
        } else false
    }

    private fun playAsManyJokersAsPossible(): DwitchGameState {
        val cardsToPlay = if (lastPlayedCards == null) {
            selectCardsFromHand(joker)
        } else {
            val numCards = lastPlayedCards.multiplicity
            selectCardsFromHand(joker, numCards)
        }
        Logger.debug { "Computer player with id $playingPlayerId plays joker(s) $cardsToPlay to avoid breaking special rule." }
        return dwitchEngine.playCards(PlayedCards(cardsToPlay))
    }

    private fun playCardsOrPassTurn(): DwitchGameState {
        return if (lastPlayedCards == null) {
            val highestMultiplicity = cardsMultiplicity.maxByOrNull { (_, multiplicity) -> multiplicity }!!.value
            val cardNamesWithHighestMultiplicity = cardsMultiplicity.filter { (_, m) -> m == highestMultiplicity }.keys
            val nameOfCardsToPlay = cardNamesWithHighestMultiplicity.sortedWith(CardNameValueAscComparator()).first()
            val cardsToPlay = selectCardsFromHand(nameOfCardsToPlay)
            playCards(cardsToPlay)
        } else {
            val numCardsToPlay = lastPlayedCards.multiplicity
            val cardsWithHighEnoughMultiplicity = selectableCardsMultiplicity
                .filterValues { multiplicity -> multiplicity >= numCardsToPlay }
            if (cardsWithHighEnoughMultiplicity.isNotEmpty()) {
                val nameOfCardsWithLowestValue =
                    cardsWithHighEnoughMultiplicity.toSortedMap(CardNameValueAscComparator()).firstKey()
                val cardsToPlay = selectCardsFromHand(nameOfCardsWithLowestValue, numCardsToPlay)
                playCards(cardsToPlay)
            } else { // No card has high enough multiplicity
                passTurn()
            }
        }
    }

    private fun computeCardsMultiplicity(): Map<CardName, Int> {
        return cardsInHand
            .groupBy({ c -> c.card.name }, { c -> c.card })
            .mapValues { (_, cards) -> cards.size }
    }

    private fun computeSelectableCardsMultiplicity(): Map<CardName, Int> {
        return cardsInHand
            .filter { c -> c.selectable }
            .groupBy({ c -> c.card.name }, { c -> c.card })
            .mapValues { (_, cards) -> cards.size }
    }

    private fun selectCardsFromHand(name: CardName, numCards: Int? = null): List<Card> {
        val matchingCards = cardsInHand
            .filter { c -> c.card.name == name }
            .map { c -> c.card }
        return if (numCards == null) matchingCards else matchingCards.take(numCards)
    }

    private fun playCards(cardsToPlay: List<Card>): DwitchGameState {
        Logger.debug { "Computer player (id $playingPlayerId) plays card(s) $cardsToPlay." }
        return dwitchEngine.playCards(PlayedCards(cardsToPlay))
    }
}