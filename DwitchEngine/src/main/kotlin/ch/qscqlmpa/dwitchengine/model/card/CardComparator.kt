package ch.qscqlmpa.dwitchengine.model.card

import ch.qscqlmpa.dwitchengine.model.info.DwitchCardInfo
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
        return CardNameValueDescComparator().compare(card1.name, card2.name)
    }
}

class DwitchCardInfoValueDescComparator : Comparator<DwitchCardInfo> {
    override fun compare(cardInfo1: DwitchCardInfo, cardInfo2: DwitchCardInfo): Int {
        return CardValueDescComparator().compare(cardInfo1.card, cardInfo2.card)
    }
}
