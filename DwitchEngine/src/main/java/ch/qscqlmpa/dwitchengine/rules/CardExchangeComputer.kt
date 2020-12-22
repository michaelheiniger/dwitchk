package ch.qscqlmpa.dwitchengine.rules

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.game.CardExchange
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import ch.qscqlmpa.dwitchengine.model.player.Rank

internal object CardExchangeComputer {

    /**
     * Compute the CardExchange for the player with the given rank and cards in hand.
     * Card exchange always occurs at the beginning of a new round when the joker is always set to CardName.TWO.
     */
    fun getCardExchange(playerId: PlayerInGameId, rank: Rank, cardsInHand: List<Card>): CardExchange? {
        checkNumCardsInHand(rank, cardsInHand)

        return when (rank) {
            Rank.President -> CardExchange(playerId, 2, getAllValuesWithoutRestriction(cardsInHand))
            Rank.VicePresident -> CardExchange(playerId, 1, getAllValuesWithoutRestriction(cardsInHand))
            Rank.Neutral -> null
            Rank.ViceAsshole -> CardExchange(playerId, 1, getValueOfNCardsWithHighestValue(cardsInHand, 1))
            Rank.Asshole -> CardExchange(playerId, 2, getValueOfNCardsWithHighestValue(cardsInHand, 2))
        }
    }

    private fun checkNumCardsInHand(rank: Rank, cardsInHand: List<Card>) = when (rank) {
        Rank.President, Rank.Asshole -> require(cardsInHand.size >= 2)
        Rank.VicePresident, Rank.ViceAsshole -> require(cardsInHand.isNotEmpty())
        else -> {
        } // Nothing to do
    }

    private fun getAllValuesWithoutRestriction(cards: List<Card>): List<CardName> = cards.map(Card::name)

    private fun getValueOfNCardsWithHighestValue(cards: List<Card>, numberOfCards: Int): List<CardName> {
        return cards.map(Card::name).sortedWith(CardNameValueDescComparator()).take(numberOfCards)
    }

    private class CardNameValueDescComparator : Comparator<CardName> {

        override fun compare(cardName1: CardName, cardName2: CardName): Int {

            // Joker has highest value
            if (cardName1 == INITIAL_JOKER) {
                return -1
            }

            // Joker has highest value
            if (cardName2 == INITIAL_JOKER) {
                return 1
            }

            // Desc
            return -cardName1.value.compareTo(cardName2.value)
        }
    }
}