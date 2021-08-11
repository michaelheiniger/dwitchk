package ch.qscqlmpa.dwitchengine.model.card

import ch.qscqlmpa.dwitchengine.model.info.DwitchCardInfo
import ch.qscqlmpa.dwitchengine.rules.initialJoker

class CardNameValueDescComparator(private val joker: CardName = initialJoker) : Comparator<CardName> {

    override fun compare(cardName1: CardName, cardName2: CardName): Int {

        // Joker has highest value
        if (cardName1 == joker) {
            return -1
        }

        // Joker has highest value
        if (cardName2 == joker) {
            return 1
        }

        // Desc
        return -cardName1.value.compareTo(cardName2.value)
    }
}

class CardNameValueAscComparator(joker: CardName = initialJoker) : Comparator<CardName> {

    private val comparator = CardNameValueDescComparator(joker)

    override fun compare(cardName1: CardName, cardName2: CardName): Int {
        return comparator.compare(cardName2, cardName1)
    }
}

class CardValueDescComparator(joker: CardName = initialJoker) : Comparator<Card> {

    private val comparator = CardNameValueDescComparator(joker)

    override fun compare(card1: Card, card2: Card): Int {
        return comparator.compare(card1.name, card2.name)
    }
}

class CardValueAscComparator(joker: CardName = initialJoker) : Comparator<Card> {

    private val comparator = CardValueDescComparator(joker)

    override fun compare(card1: Card, card2: Card): Int {
        return comparator.compare(card2, card1)
    }
}

class DwitchCardInfoValueDescComparator(joker: CardName = initialJoker) : Comparator<DwitchCardInfo> {

    private val comparator = CardValueDescComparator(joker)

    override fun compare(cardInfo1: DwitchCardInfo, cardInfo2: DwitchCardInfo): Int {
        return comparator.compare(cardInfo1.card, cardInfo2.card)
    }
}

class DwitchCardInfoValueAscComparator(joker: CardName = initialJoker) : Comparator<DwitchCardInfo> {

    private val comparator = CardValueDescComparator(joker)

    override fun compare(cardInfo1: DwitchCardInfo, cardInfo2: DwitchCardInfo): Int {
        return comparator.compare(cardInfo2.card, cardInfo1.card)
    }
}
