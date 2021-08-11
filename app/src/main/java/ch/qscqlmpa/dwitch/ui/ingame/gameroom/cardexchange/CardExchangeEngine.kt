package ch.qscqlmpa.dwitch.ui.ingame.gameroom.cardexchange

import ch.qscqlmpa.dwitch.ui.ingame.gameroom.CardInfo
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardValueDescComparator
import ch.qscqlmpa.dwitchstore.ingamestore.model.CardExchangeInfo
import org.tinylog.kotlin.Logger

data class CardExchangeState(
    val numCardsToChoose: NumCardsToExchange,
    val cardsInHand: List<CardInfo>,
    val canPerformExchange: Boolean
)

sealed class NumCardsToExchange {
    object One : NumCardsToExchange()
    object Two : NumCardsToExchange()
}

class CardExchangeEngine(cardExchangeInfo: CardExchangeInfo) {
    private val cardComparator = CardValueDescComparator()
    private val numCardsToChoose = cardExchangeInfo.cardExchange.numCardsToChoose
    private val allowedCardValues = cardExchangeInfo.cardExchange.allowedCardValues.toMutableList()
    private val cardsInHand: List<Card> = cardExchangeInfo.cardsInHand
    private val cardsSelected = mutableListOf<Card>()

    fun onCardToExchangeClick(card: Card) {
        if (cardsSelected.contains(card)) removeCardFromExchange(card)
        else addCardToExchange(card)
    }

    fun getCardExchangeState(): CardExchangeState {
        val numCardForExchange = when (numCardsToChoose) {
            1 -> NumCardsToExchange.One
            2 -> NumCardsToExchange.Two
            else -> throw IllegalStateException("Number of cards to choose for exchange can only be one or two")
        }
        return CardExchangeState(
            numCardForExchange,
            cardsInHand.sortedWith(cardComparator).map { c ->
                CardInfo(c, selectable = isCardSelectable(c), selected = cardsSelected.contains(c))
            },
            canPerformExchange = cardsSelected.size == numCardsToChoose
        )
    }

    fun getCardsToExchange(): Set<Card> {
        return getCardExchangeState().cardsInHand.filter { c -> c.selected }.map(CardInfo::card).toSet()
    }

    private fun removeCardFromExchange(card: Card) {
        if (!cardsSelected.remove(card)) throw IllegalArgumentException("Card $card is not in the chosen cards !")
        allowedCardValues.add(card.name)
    }

    private fun addCardToExchange(card: Card) {
        require(allowedCardValues.contains(card.name)) { "Card $card cannot be chosen because it has a too low value (allowed card values: $allowedCardValues)" }

        allowedCardValues.remove(card.name)
        if (cardsSelected.size == numCardsToChoose) {
            Logger.warn { "No more card can be chosen: already ${cardsSelected.size} chosen." }
            return
        }
        cardsSelected.add(card)
    }

    private fun isCardSelectable(card: Card) = allowedCardValues.contains(card.name) || cardsSelected.contains(card)
}
