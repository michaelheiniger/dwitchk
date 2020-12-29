package ch.qscqlmpa.dwitchengine.rules

import ch.qscqlmpa.dwitchengine.model.player.PlayerDone
import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId
import ch.qscqlmpa.dwitchengine.model.player.Rank

internal object RankComputer {

    fun computePlayersRank(players: List<PlayerDone>): Map<PlayerDwitchId, Rank> {

        val numPlayers = players.size
        val rankMap = mutableMapOf<PlayerDwitchId, Rank>()
        val (playersWhoFinishedWithJoker, playersWhoDidntFinishWithJoker) = players.partition { lm -> lm.cardPlayedIsJoker }

        playersWhoFinishedWithJoker
                .reversed()
                .mapIndexed { index, lm ->
                    val position = index + 1
                    when (numPlayers) {
                        0, 1 -> throw IllegalStateException("There cannot be less than 2 players in the game.")
                        2 -> assignPunitiveRankWhenTwoPlayersForPosition(position, lm.playerId)
                        3 -> assignPunitiveRankWhenThreePlayersForPosition(position, lm.playerId)
                        4 -> assignPunitiveRankWhenFourPlayersForPosition(position, lm.playerId)
                        else -> assignPunitiveRankWhenMoreThanFourPlayersForPosition(position, lm.playerId, numPlayers)
                    }
                }
                .toMap(rankMap)

        playersWhoDidntFinishWithJoker.mapIndexed { index, lm ->
            val position = index + 1
            when (numPlayers) {
                0, 1 -> throw IllegalStateException("There cannot be less than 2 players in the game.")
                2 -> assignRankWhenTwoPlayersForPosition(position, lm.playerId)
                3 -> assignRankWhenThreePlayersForPosition(position, lm.playerId)
                4 -> assignRankWhenFourPlayersForPosition(position, lm.playerId)
                else -> assignRankWhenMoreThanFourPlayersForPosition(position, lm.playerId, numPlayers)
            }
        }.toMap(rankMap)
        return rankMap.toMap()
    }

    private fun assignPunitiveRankWhenTwoPlayersForPosition(position: Int, playerId: PlayerDwitchId): Pair<PlayerDwitchId, Rank> {
        return when (position) {
            1 -> playerId to Rank.Asshole
            else -> throw IllegalStateException("At most one player can finish with joker when number players is two")
        }
    }

    private fun assignPunitiveRankWhenThreePlayersForPosition(position: Int, playerId: PlayerDwitchId): Pair<PlayerDwitchId, Rank> {
        return when (position) {
            1 -> playerId to Rank.Asshole
            2 -> playerId to Rank.Neutral
            else -> throw IllegalStateException("At most two players can finish with joker when number players is three")
        }
    }

    private fun assignPunitiveRankWhenFourPlayersForPosition(position: Int, playerId: PlayerDwitchId): Pair<PlayerDwitchId, Rank> {
        return when (position) {
            1 -> playerId to Rank.Asshole
            2 -> playerId to Rank.ViceAsshole
            3 -> playerId to Rank.VicePresident
            else -> throw IllegalStateException("At most three players can finish with joker when number players is four")
        }
    }

    private fun assignPunitiveRankWhenMoreThanFourPlayersForPosition(position: Int, playerId: PlayerDwitchId, numPlayers: Int): Pair<PlayerDwitchId, Rank> {
        return when (position) {
            1 -> playerId to Rank.Asshole
            2 -> playerId to Rank.ViceAsshole
            in 3 until (numPlayers - 1) -> playerId to Rank.Neutral
            numPlayers - 1 -> playerId to Rank.VicePresident
            else -> throw IllegalStateException("At most ${numPlayers - 1} players can finish with a joker")
        }
    }

    private fun assignRankWhenTwoPlayersForPosition(position: Int, playerId: PlayerDwitchId): Pair<PlayerDwitchId, Rank> {
        return when (position) {
            1 -> playerId to Rank.President
            2 -> playerId to Rank.Asshole
            else -> throw IllegalStateException("There are only two ranks to assign when number of players is two")
        }
    }

    private fun assignRankWhenThreePlayersForPosition(position: Int, playerId: PlayerDwitchId): Pair<PlayerDwitchId, Rank> {
        return when (position) {
            1 -> playerId to Rank.President
            2 -> playerId to Rank.Neutral
            3 -> playerId to Rank.Asshole
            else -> throw IllegalStateException("There are only three ranks to assign when number of players is three")
        }
    }

    private fun assignRankWhenFourPlayersForPosition(position: Int, playerId: PlayerDwitchId): Pair<PlayerDwitchId, Rank> {
        return when (position) {
            1 -> playerId to Rank.President
            2 -> playerId to Rank.VicePresident
            3 -> playerId to Rank.ViceAsshole
            4 -> playerId to Rank.Asshole
            else -> throw IllegalStateException("There are only four ranks to assign when number of players is four")
        }
    }

    private fun assignRankWhenMoreThanFourPlayersForPosition(position: Int, playerId: PlayerDwitchId, numPlayers: Int): Pair<PlayerDwitchId, Rank> {
        return when (position) {
            1 -> playerId to Rank.President
            2 -> playerId to Rank.VicePresident
            in 3 until numPlayers - 1 -> playerId to Rank.Neutral
            numPlayers - 1 -> playerId to Rank.ViceAsshole
            numPlayers -> playerId to Rank.Asshole
            else -> throw IllegalStateException("There are only $numPlayers ranks to assign when number of players is $numPlayers")
        }
    }
}