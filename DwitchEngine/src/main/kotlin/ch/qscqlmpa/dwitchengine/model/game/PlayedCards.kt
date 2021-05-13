package ch.qscqlmpa.dwitchengine.model.game

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import kotlinx.serialization.Serializable

@Serializable
data class PlayedCards(val cards: List<Card>) {
    constructor(vararg cards: Card) : this(listOf(*cards))

    val name: CardName
    val value: Int
    val multiplicity = cards.size

    init {
        require(cards.size in 1..4) { "Must contain between 1 and 4 cards" }
        val cardNames = cards.map(Card::name).toSet()
        require(cardNames.size == 1) { "All cards played must have the same card name. Actual names are $cardNames" }
        name = cardNames.first()
        value = name.value
    }
}