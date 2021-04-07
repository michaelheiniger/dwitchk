package ch.qscqlmpa.dwitchengine.rules

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.card.CardNameValueDescComparator
import ch.qscqlmpa.dwitchengine.model.game.DwitchCardExchange
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank

internal object CardExchangeComputer {

    /**
     * Compute the CardExchange for the player with the given rank and cards in hand.
     * Card exchange always occurs at the beginning of a new round when the joker is always set to CardName.TWO.
     */
    fun getCardExchange(playerId: DwitchPlayerId, rank: DwitchRank, cardsInHand: Set<Card>): DwitchCardExchange? {
        checkNumCardsInHand(rank, cardsInHand)

        return when (rank) {
            DwitchRank.President -> DwitchCardExchange(playerId, 2, getAllValuesWithoutRestriction(cardsInHand))
            DwitchRank.VicePresident -> DwitchCardExchange(playerId, 1, getAllValuesWithoutRestriction(cardsInHand))
            DwitchRank.Neutral -> null
            DwitchRank.ViceAsshole -> DwitchCardExchange(playerId, 1, getValueOfNCardsWithHighestValue(cardsInHand, 1))
            DwitchRank.Asshole -> DwitchCardExchange(playerId, 2, getValueOfNCardsWithHighestValue(cardsInHand, 2))
        }
    }

    fun getValueOfNCardsWithHighestValue(
        cards: Set<Card>,
        numberOfCards: Int
    ): List<CardName> { // Want to keep duplicated "names"
        return cards.map(Card::name).sortedWith(CardNameValueDescComparator()).take(numberOfCards)
    }

    private fun checkNumCardsInHand(rank: DwitchRank, cardsInHand: Set<Card>) = when (rank) {
        DwitchRank.President, DwitchRank.Asshole -> require(cardsInHand.size >= 2)
        DwitchRank.VicePresident, DwitchRank.ViceAsshole -> require(cardsInHand.isNotEmpty())
        else -> {
        } // Nothing to do
    }

    private fun getAllValuesWithoutRestriction(cards: Set<Card>): List<CardName> =
        cards.map(Card::name) // Want to keep duplicated "names"
}
