package ch.qscqlmpa.dwitch.ui

import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitchengine.model.card.Card

object CardResourceMapper {

    fun resourceForCard(card: Card): Int {
        return when(card) {
            Card.Clubs2 -> R.drawable.clubs_2
            Card.Clubs3 -> R.drawable.clubs_3
            Card.Clubs4 -> R.drawable.clubs_4
            Card.Clubs5 -> R.drawable.clubs_5
            Card.Clubs6 -> R.drawable.clubs_6
            Card.Clubs7 -> R.drawable.clubs_7
            Card.Clubs8 -> R.drawable.clubs_8
            Card.Clubs9 -> R.drawable.clubs_9
            Card.Clubs10 -> R.drawable.clubs_10
            Card.ClubsJack -> R.drawable.clubs_jack
            Card.ClubsQueen -> R.drawable.clubs_queen
            Card.ClubsKing -> R.drawable.clubs_king
            Card.ClubsAce -> R.drawable.clubs_ace
            Card.Spades2 -> R.drawable.spades_2
            Card.Spades3 -> R.drawable.spades_3
            Card.Spades4 -> R.drawable.spades_4
            Card.Spades5 -> R.drawable.spades_5
            Card.Spades6 -> R.drawable.spades_6
            Card.Spades7 -> R.drawable.spades_7
            Card.Spades8 -> R.drawable.spades_8
            Card.Spades9 -> R.drawable.spades_9
            Card.Spades10 -> R.drawable.spades_10
            Card.SpadesJack -> R.drawable.spades_jack
            Card.SpadesQueen -> R.drawable.spades_queen
            Card.SpadesKing -> R.drawable.spades_king
            Card.SpadesAce -> R.drawable.spades_ace
            Card.Hearts2 -> R.drawable.hearts_2
            Card.Hearts3 -> R.drawable.hearts_3
            Card.Hearts4 -> R.drawable.hearts_4
            Card.Hearts5 -> R.drawable.hearts_5
            Card.Hearts6 -> R.drawable.hearts_6
            Card.Hearts7 -> R.drawable.hearts_7
            Card.Hearts8 -> R.drawable.hearts_8
            Card.Hearts9 -> R.drawable.hearts_9
            Card.Hearts10 -> R.drawable.hearts_10
            Card.HeartsJack -> R.drawable.hearts_jack
            Card.HeartsQueen -> R.drawable.hearts_queen
            Card.HeartsKing -> R.drawable.hearts_king
            Card.HeartsAce -> R.drawable.hearts_ace
            Card.Diamonds2 -> R.drawable.diamonds_2
            Card.Diamonds3 -> R.drawable.diamonds_3
            Card.Diamonds4 -> R.drawable.diamonds_4
            Card.Diamonds5 -> R.drawable.diamonds_5
            Card.Diamonds6 -> R.drawable.diamonds_6
            Card.Diamonds7 -> R.drawable.diamonds_7
            Card.Diamonds8 -> R.drawable.diamonds_8
            Card.Diamonds9 -> R.drawable.diamonds_9
            Card.Diamonds10 -> R.drawable.diamonds_10
            Card.DiamondsJack -> R.drawable.diamonds_jack
            Card.DiamondsQueen -> R.drawable.diamonds_queen
            Card.DiamondsKing -> R.drawable.diamonds_king
            Card.DiamondsAce -> R.drawable.diamonds_ace
            Card.Blank -> R.drawable.back
        }
    }
}