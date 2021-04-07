package ch.qscqlmpa.dwitchengine.model.card

import ch.qscqlmpa.dwitchengine.rules.INITIAL_JOKER

class CardNameValueDescComparator : Comparator<CardName> {

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

class CardValueDescComparator : Comparator<Card> {

    override fun compare(card1: Card, card2: Card): Int {

        // Joker has highest value
        if (card1.value() == INITIAL_JOKER.value) {
            return -1
        }

        // Joker has highest value
        if (card2.value() == INITIAL_JOKER.value) {
            return 1
        }

        // Desc
        return -card1.value().compareTo(card2.value())
    }
}