package ch.qscqlmpa.dwitchengine.model.card

object CardUtil {

    fun fromId(id: CardId): Card {
        return cardMap.getValue(id)
    }

    fun getAllCardsExcept(cardsToOmit: List<Card>): List<Card> {
        return deck.filter { card -> !cardsToOmit.contains(card) }
    }

    fun getAllCardsExcept(cardsToOmit: Set<Card>): Set<Card> {
        return deck.filter { card -> !cardsToOmit.contains(card) }.toSet()
    }

    val deck = listOf(
        Card.Clubs2,
        Card.Clubs3,
        Card.Clubs4,
        Card.Clubs5,
        Card.Clubs6,
        Card.Clubs7,
        Card.Clubs8,
        Card.Clubs9,
        Card.Clubs10,
        Card.ClubsJack,
        Card.ClubsQueen,
        Card.ClubsKing,
        Card.ClubsAce,
        Card.Spades2,
        Card.Spades3,
        Card.Spades4,
        Card.Spades5,
        Card.Spades6,
        Card.Spades7,
        Card.Spades8,
        Card.Spades9,
        Card.Spades10,
        Card.SpadesJack,
        Card.SpadesQueen,
        Card.SpadesKing,
        Card.SpadesAce,
        Card.Hearts2,
        Card.Hearts3,
        Card.Hearts4,
        Card.Hearts5,
        Card.Hearts6,
        Card.Hearts7,
        Card.Hearts8,
        Card.Hearts9,
        Card.Hearts10,
        Card.HeartsJack,
        Card.HeartsQueen,
        Card.HeartsKing,
        Card.HeartsAce,
        Card.Diamonds2,
        Card.Diamonds3,
        Card.Diamonds4,
        Card.Diamonds5,
        Card.Diamonds6,
        Card.Diamonds7,
        Card.Diamonds8,
        Card.Diamonds9,
        Card.Diamonds10,
        Card.DiamondsJack,
        Card.DiamondsQueen,
        Card.DiamondsKing,
        Card.DiamondsAce
    )

    val deckSize = deck.size

    private val cardMap: Map<CardId, Card> = mapOf(
        Card.Clubs2.id to Card.Clubs2,
        Card.Clubs3.id to Card.Clubs3,
        Card.Clubs4.id to Card.Clubs4,
        Card.Clubs5.id to Card.Clubs5,
        Card.Clubs6.id to Card.Clubs6,
        Card.Clubs7.id to Card.Clubs7,
        Card.Clubs8.id to Card.Clubs8,
        Card.Clubs9.id to Card.Clubs9,
        Card.Clubs10.id to Card.Clubs10,
        Card.ClubsJack.id to Card.ClubsJack,
        Card.ClubsQueen.id to Card.ClubsQueen,
        Card.ClubsKing.id to Card.ClubsKing,
        Card.ClubsAce.id to Card.ClubsAce,
        Card.Spades2.id to Card.Spades2,
        Card.Spades3.id to Card.Spades3,
        Card.Spades4.id to Card.Spades4,
        Card.Spades5.id to Card.Spades5,
        Card.Spades6.id to Card.Spades6,
        Card.Spades7.id to Card.Spades7,
        Card.Spades8.id to Card.Spades8,
        Card.Spades9.id to Card.Spades9,
        Card.Spades10.id to Card.Spades10,
        Card.SpadesJack.id to Card.SpadesJack,
        Card.SpadesQueen.id to Card.SpadesQueen,
        Card.SpadesKing.id to Card.SpadesKing,
        Card.SpadesAce.id to Card.SpadesAce,
        Card.Hearts2.id to Card.Hearts2,
        Card.Hearts3.id to Card.Hearts3,
        Card.Hearts4.id to Card.Hearts4,
        Card.Hearts5.id to Card.Hearts5,
        Card.Hearts6.id to Card.Hearts6,
        Card.Hearts7.id to Card.Hearts7,
        Card.Hearts8.id to Card.Hearts8,
        Card.Hearts9.id to Card.Hearts9,
        Card.Hearts10.id to Card.Hearts10,
        Card.HeartsJack.id to Card.HeartsJack,
        Card.HeartsQueen.id to Card.HeartsQueen,
        Card.HeartsKing.id to Card.HeartsKing,
        Card.HeartsAce.id to Card.HeartsAce,
        Card.Diamonds2.id to Card.Diamonds2,
        Card.Diamonds3.id to Card.Diamonds3,
        Card.Diamonds4.id to Card.Diamonds4,
        Card.Diamonds5.id to Card.Diamonds5,
        Card.Diamonds6.id to Card.Diamonds6,
        Card.Diamonds7.id to Card.Diamonds7,
        Card.Diamonds8.id to Card.Diamonds8,
        Card.Diamonds9.id to Card.Diamonds9,
        Card.Diamonds10.id to Card.Diamonds10,
        Card.DiamondsJack.id to Card.DiamondsJack,
        Card.DiamondsQueen.id to Card.DiamondsQueen,
        Card.DiamondsKing.id to Card.DiamondsKing,
        Card.DiamondsAce.id to Card.DiamondsAce,
        Card.Blank.id to Card.Blank
    )
}
