package ch.qscqlmpa.dwitch.ui.ongoinggame.cardexchange

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardValueDescComparator
import ch.qscqlmpa.dwitchengine.model.info.CardItem
import ch.qscqlmpa.dwitchstore.ingamestore.model.CardExchangeInfo
import org.tinylog.kotlin.Logger

data class CardExchangeState(
    val numCardsToChoose: NumCardForExchange,
    val cardsInHand: List<CardItem>,
    val cardsToExchange: List<CardItem>,
    val canPerformExchange: Boolean
)

sealed class NumCardForExchange {
    object One : NumCardForExchange()
    object Two : NumCardForExchange()
}

class CardExchangeStateEngine(cardExchangeInfo: CardExchangeInfo) {

    private val cardComparator = CardValueDescComparator()
    private val numCardsToChoose = cardExchangeInfo.cardExchange.numCardsToChoose
    private val allowedCardValues = cardExchangeInfo.cardExchange.allowedCardValues.toMutableList()
    private val cardsInHand: MutableList<Card> = cardExchangeInfo.cardsInHand.toMutableList()
    private val cardsToExchange: MutableList<Card> = mutableListOf()

    fun addCardToExchange(card: Card) {
        if (!allowedCardValues.contains(card.name)) {
            Logger.warn { "Card $card cannot be chosen because it has a too low value (allowed card values: $allowedCardValues)" }
            return
        }
        allowedCardValues.remove(card.name)
        if (cardsToExchange.size == numCardsToChoose) {
            Logger.warn { "No more card can be chosen: already ${cardsToExchange.size} chosen." }
            return
        }
        if (cardsInHand.remove(card)) {
            cardsToExchange.add(card)
        } else {
            throw IllegalArgumentException("Card $card is not in the hand !")
        }
    }

    fun removeCardFromExchange(card: Card) {
        if (cardsToExchange.remove(card)) {
            cardsInHand.add(card)
        } else {
            throw IllegalArgumentException("Card $card is not in the chosen cards !")
        }
        allowedCardValues.add(card.name)
    }

    fun getCardExchangeState(): CardExchangeState {
        val numCardForExchange = when (numCardsToChoose) {
            1 -> NumCardForExchange.One
            2 -> NumCardForExchange.Two
            else -> throw IllegalStateException("Number of cards to choose for exchange can only be one or two")
        }

        return CardExchangeState(
            numCardForExchange,
            cardsInHand.sortedWith(cardComparator).map { c -> CardItem(c, isCardSelectable(c)) },
            cardsToExchange.map { c -> CardItem(c, true) },
            canPerformExchange = cardsToExchange.size == numCardsToChoose
        )
    }

    private fun isCardSelectable(card: Card): Boolean {
        val cardHasAllowedValue = allowedCardValues.contains(card.name)
//        Logger.trace { "Is card $card selectable ? ${card.value()} is in $allowedCardValues : $cardHasAllowedValue" }
        return cardHasAllowedValue
    }
}