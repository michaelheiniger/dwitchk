package ch.qscqlmpa.dwitchengine.model.card

import kotlinx.serialization.Serializable

@Serializable
data class CardId(val value: Int)

@Serializable
sealed class Card(val id: CardId, val name: CardName, val suit: CardSuit) {

    @Serializable
    object Clubs2 : Card(CardId(0), CardName.Two, CardSuit.Clubs)

    @Serializable
    object Clubs3 : Card(CardId(1), CardName.Three, CardSuit.Clubs)

    @Serializable
    object Clubs4 : Card(CardId(2), CardName.Four, CardSuit.Clubs)

    @Serializable
    object Clubs5 : Card(CardId(3), CardName.Five, CardSuit.Clubs)

    @Serializable
    object Clubs6 : Card(CardId(4), CardName.Six, CardSuit.Clubs)

    @Serializable
    object Clubs7 : Card(CardId(5), CardName.Seven, CardSuit.Clubs)

    @Serializable
    object Clubs8 : Card(CardId(6), CardName.Eight, CardSuit.Clubs)

    @Serializable
    object Clubs9 : Card(CardId(7), CardName.Nine, CardSuit.Clubs)

    @Serializable
    object Clubs10 : Card(CardId(8), CardName.Ten, CardSuit.Clubs)

    @Serializable
    object ClubsJack : Card(CardId(9), CardName.Jack, CardSuit.Clubs)

    @Serializable
    object ClubsQueen : Card(CardId(10), CardName.Queen, CardSuit.Clubs)

    @Serializable
    object ClubsKing : Card(CardId(11), CardName.King, CardSuit.Clubs)

    @Serializable
    object ClubsAce : Card(CardId(12), CardName.Ace, CardSuit.Clubs)

    @Serializable
    object Spades2 : Card(CardId(13), CardName.Two, CardSuit.Spades)

    @Serializable
    object Spades3 : Card(CardId(14), CardName.Three, CardSuit.Spades)

    @Serializable
    object Spades4 : Card(CardId(15), CardName.Four, CardSuit.Spades)

    @Serializable
    object Spades5 : Card(CardId(16), CardName.Five, CardSuit.Spades)

    @Serializable
    object Spades6 : Card(CardId(17), CardName.Six, CardSuit.Spades)

    @Serializable
    object Spades7 : Card(CardId(18), CardName.Seven, CardSuit.Spades)

    @Serializable
    object Spades8 : Card(CardId(19), CardName.Eight, CardSuit.Spades)

    @Serializable
    object Spades9 : Card(CardId(20), CardName.Nine, CardSuit.Spades)

    @Serializable
    object Spades10 : Card(CardId(21), CardName.Ten, CardSuit.Spades)

    @Serializable
    object SpadesJack : Card(CardId(22), CardName.Jack, CardSuit.Spades)

    @Serializable
    object SpadesQueen : Card(CardId(23), CardName.Queen, CardSuit.Spades)

    @Serializable
    object SpadesKing : Card(CardId(24), CardName.King, CardSuit.Spades)

    @Serializable
    object SpadesAce : Card(CardId(25), CardName.Ace, CardSuit.Spades)

    @Serializable
    object Hearts2 : Card(CardId(26), CardName.Two, CardSuit.Hearts)

    @Serializable
    object Hearts3 : Card(CardId(27), CardName.Three, CardSuit.Hearts)

    @Serializable
    object Hearts4 : Card(CardId(28), CardName.Four, CardSuit.Hearts)

    @Serializable
    object Hearts5 : Card(CardId(29), CardName.Five, CardSuit.Hearts)

    @Serializable
    object Hearts6 : Card(CardId(30), CardName.Six, CardSuit.Hearts)

    @Serializable
    object Hearts7 : Card(CardId(31), CardName.Seven, CardSuit.Hearts)

    @Serializable
    object Hearts8 : Card(CardId(32), CardName.Eight, CardSuit.Hearts)

    @Serializable
    object Hearts9 : Card(CardId(33), CardName.Nine, CardSuit.Hearts)

    @Serializable
    object Hearts10 : Card(CardId(34), CardName.Ten, CardSuit.Hearts)

    @Serializable
    object HeartsJack : Card(CardId(35), CardName.Jack, CardSuit.Hearts)

    @Serializable
    object HeartsQueen : Card(CardId(36), CardName.Queen, CardSuit.Hearts)

    @Serializable
    object HeartsKing : Card(CardId(37), CardName.King, CardSuit.Hearts)

    @Serializable
    object HeartsAce : Card(CardId(38), CardName.Ace, CardSuit.Hearts)

    @Serializable
    object Diamonds2 : Card(CardId(39), CardName.Two, CardSuit.Diamonds)

    @Serializable
    object Diamonds3 : Card(CardId(40), CardName.Three, CardSuit.Diamonds)

    @Serializable
    object Diamonds4 : Card(CardId(41), CardName.Four, CardSuit.Diamonds)

    @Serializable
    object Diamonds5 : Card(CardId(42), CardName.Five, CardSuit.Diamonds)

    @Serializable
    object Diamonds6 : Card(CardId(43), CardName.Six, CardSuit.Diamonds)

    @Serializable
    object Diamonds7 : Card(CardId(44), CardName.Seven, CardSuit.Diamonds)

    @Serializable
    object Diamonds8 : Card(CardId(45), CardName.Eight, CardSuit.Diamonds)

    @Serializable
    object Diamonds9 : Card(CardId(46), CardName.Nine, CardSuit.Diamonds)

    @Serializable
    object Diamonds10 : Card(CardId(47), CardName.Ten, CardSuit.Diamonds)

    @Serializable
    object DiamondsJack : Card(CardId(48), CardName.Jack, CardSuit.Diamonds)

    @Serializable
    object DiamondsQueen : Card(CardId(49), CardName.Queen, CardSuit.Diamonds)

    @Serializable
    object DiamondsKing : Card(CardId(50), CardName.King, CardSuit.Diamonds)

    @Serializable
    object DiamondsAce : Card(CardId(51), CardName.Ace, CardSuit.Diamonds)

    @Serializable
    object Blank : Card(CardId(52), CardName.Blank, CardSuit.Blank)

    fun value(): Int {
        return name.value
    }

    override fun toString(): String {
        return "${name.name} of ${suit.name} (id: ${id.value})"
    }
}
