package ch.qscqlmpa.dwitch.ui

import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerStatus
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank

object ResourceMapper {

    fun getResourceLong(rank: DwitchRank): Int {
        return when (rank) {
            DwitchRank.President -> R.string.president_long
            DwitchRank.VicePresident -> R.string.vice_president_long
            DwitchRank.Neutral -> R.string.neutral_long
            DwitchRank.ViceAsshole -> R.string.vice_asshole_long
            DwitchRank.Asshole -> R.string.asshole_long
        }
    }

    fun getImageResource(status: DwitchPlayerStatus): Int {
        return when (status) {
            DwitchPlayerStatus.Done -> R.string.player_status_done
            DwitchPlayerStatus.Playing -> R.string.player_status_playing
            DwitchPlayerStatus.TurnPassed -> R.string.player_status_turnPassed
            DwitchPlayerStatus.Waiting -> R.string.player_status_waiting
        }
    }

    fun getImageResource(card: Card): Int {
        return when (card) {
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

    fun getContentDescriptionResource(name: CardName): Int {
        return when (name) {
            CardName.Ace -> R.string.ace_content_description
            CardName.Blank -> R.string.blank_content_description
            CardName.Eight -> R.string.eight_content_description
            CardName.Five -> R.string.five_content_description
            CardName.Four -> R.string.four_content_description
            CardName.Jack -> R.string.jack_content_description
            CardName.King -> R.string.king_content_description
            CardName.Nine -> R.string.nine_content_description
            CardName.Queen -> R.string.queen_content_description
            CardName.Seven -> R.string.seven_content_description
            CardName.Six -> R.string.six_content_description
            CardName.Ten -> R.string.ten_content_description
            CardName.Three -> R.string.three_content_description
            CardName.Two -> R.string.two_content_description
        }
    }

    fun getContentDescriptionResource(card: Card): Int {
        return when (card) {
            Card.Clubs2 -> R.string.clubs_2_content_description
            Card.Clubs3 -> R.string.clubs_3_content_description
            Card.Clubs4 -> R.string.clubs_4_content_description
            Card.Clubs5 -> R.string.clubs_5_content_description
            Card.Clubs6 -> R.string.clubs_6_content_description
            Card.Clubs7 -> R.string.clubs_7_content_description
            Card.Clubs8 -> R.string.clubs_8_content_description
            Card.Clubs9 -> R.string.clubs_9_content_description
            Card.Clubs10 -> R.string.clubs_10_content_description
            Card.ClubsJack -> R.string.clubs_jack_content_description
            Card.ClubsQueen -> R.string.clubs_queen_content_description
            Card.ClubsKing -> R.string.clubs_king_content_description
            Card.ClubsAce -> R.string.clubs_ace_content_description
            Card.Spades2 -> R.string.spades_2_content_description
            Card.Spades3 -> R.string.spades_3_content_description
            Card.Spades4 -> R.string.spades_4_content_description
            Card.Spades5 -> R.string.spades_5_content_description
            Card.Spades6 -> R.string.spades_6_content_description
            Card.Spades7 -> R.string.spades_7_content_description
            Card.Spades8 -> R.string.spades_8_content_description
            Card.Spades9 -> R.string.spades_9_content_description
            Card.Spades10 -> R.string.spades_10_content_description
            Card.SpadesJack -> R.string.spades_jack_content_description
            Card.SpadesQueen -> R.string.spades_queen_content_description
            Card.SpadesKing -> R.string.spades_king_content_description
            Card.SpadesAce -> R.string.spades_ace_content_description
            Card.Hearts2 -> R.string.hearts_2_content_description
            Card.Hearts3 -> R.string.hearts_3_content_description
            Card.Hearts4 -> R.string.hearts_4_content_description
            Card.Hearts5 -> R.string.hearts_5_content_description
            Card.Hearts6 -> R.string.hearts_6_content_description
            Card.Hearts7 -> R.string.hearts_7_content_description
            Card.Hearts8 -> R.string.hearts_8_content_description
            Card.Hearts9 -> R.string.hearts_9_content_description
            Card.Hearts10 -> R.string.hearts_10_content_description
            Card.HeartsJack -> R.string.hearts_jack_content_description
            Card.HeartsQueen -> R.string.hearts_queen_content_description
            Card.HeartsKing -> R.string.hearts_king_content_description
            Card.HeartsAce -> R.string.hearts_ace_content_description
            Card.Diamonds2 -> R.string.diamonds_2_content_description
            Card.Diamonds3 -> R.string.diamonds_3_content_description
            Card.Diamonds4 -> R.string.diamonds_4_content_description
            Card.Diamonds5 -> R.string.diamonds_5_content_description
            Card.Diamonds6 -> R.string.diamonds_6_content_description
            Card.Diamonds7 -> R.string.diamonds_7_content_description
            Card.Diamonds8 -> R.string.diamonds_8_content_description
            Card.Diamonds9 -> R.string.diamonds_9_content_description
            Card.Diamonds10 -> R.string.diamonds_10_content_description
            Card.DiamondsJack -> R.string.diamonds_jack_content_description
            Card.DiamondsQueen -> R.string.diamonds_queen_content_description
            Card.DiamondsKing -> R.string.diamonds_king_content_description
            Card.DiamondsAce -> R.string.diamonds_ace_content_description
            Card.Blank -> R.string.back_content_description
        }
    }
}
