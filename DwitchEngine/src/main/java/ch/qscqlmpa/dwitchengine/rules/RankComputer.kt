package ch.qscqlmpa.dwitchengine.rules

import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank
import ch.qscqlmpa.dwitchengine.model.player.SpecialRuleBreaker

internal object RankComputer {

    fun computePlayersRank(
        playersFinishOrder: List<DwitchPlayerId>,
        specialRuleBreakers: List<SpecialRuleBreaker>
    ): Map<DwitchPlayerId, DwitchRank> {

        val numPlayers = playersFinishOrder.size
        val rankMap = mutableMapOf<DwitchPlayerId, DwitchRank>()

        specialRuleBreakers
            .map { ruleBreaker -> ruleBreaker.playerId }
            .reversed()
            .distinct() // In case a player broke more than one rule, keep the last occurrence (.reversed())
            .mapIndexed { index, ruleBreakerId ->
                val position = index + 1
                when (numPlayers) {
                    0, 1 -> throw IllegalStateException("There cannot be less than 2 players in the game.")
                    2 -> assignPunitiveRankWhenTwoPlayersForPosition(position, ruleBreakerId)
                    3 -> assignPunitiveRankWhenThreePlayersForPosition(position, ruleBreakerId)
                    4 -> assignPunitiveRankWhenFourPlayersForPosition(position, ruleBreakerId)
                    else -> assignPunitiveRankWhenMoreThanFourPlayersForPosition(position, ruleBreakerId, numPlayers)
                }
            }
            .toMap(rankMap)

        val otherPlayers = playersFinishOrder.filter { id -> !specialRuleBreakers.map { r -> r.playerId }.contains(id) }

        otherPlayers.mapIndexed { index, playerId ->
            val position = index + 1
            when (numPlayers) {
                0, 1 -> throw IllegalStateException("There cannot be less than 2 players in the game.")
                2 -> assignRankWhenTwoPlayersForPosition(position, playerId)
                3 -> assignRankWhenThreePlayersForPosition(position, playerId)
                4 -> assignRankWhenFourPlayersForPosition(position, playerId)
                else -> assignRankWhenMoreThanFourPlayersForPosition(position, playerId, numPlayers)
            }
        }.toMap(rankMap)
        return rankMap.toMap()
    }

    private fun assignPunitiveRankWhenTwoPlayersForPosition(
        position: Int,
        playerId: DwitchPlayerId
    ): Pair<DwitchPlayerId, DwitchRank> {
        return when (position) {
            1 -> playerId to DwitchRank.Asshole
            else -> throw IllegalStateException("At most one player can finish with joker when number players is two")
        }
    }

    private fun assignPunitiveRankWhenThreePlayersForPosition(
        position: Int,
        playerId: DwitchPlayerId
    ): Pair<DwitchPlayerId, DwitchRank> {
        return when (position) {
            1 -> playerId to DwitchRank.Asshole
            2 -> playerId to DwitchRank.Neutral
            else -> throw IllegalStateException("At most two players can finish with joker when number players is three")
        }
    }

    private fun assignPunitiveRankWhenFourPlayersForPosition(
        position: Int,
        playerId: DwitchPlayerId
    ): Pair<DwitchPlayerId, DwitchRank> {
        return when (position) {
            1 -> playerId to DwitchRank.Asshole
            2 -> playerId to DwitchRank.ViceAsshole
            3 -> playerId to DwitchRank.VicePresident
            else -> throw IllegalStateException("At most three players can finish with joker when number players is four")
        }
    }

    private fun assignPunitiveRankWhenMoreThanFourPlayersForPosition(
        position: Int,
        playerId: DwitchPlayerId,
        numPlayers: Int
    ): Pair<DwitchPlayerId, DwitchRank> {
        return when (position) {
            1 -> playerId to DwitchRank.Asshole
            2 -> playerId to DwitchRank.ViceAsshole
            in 3 until (numPlayers - 1) -> playerId to DwitchRank.Neutral
            numPlayers - 1 -> playerId to DwitchRank.VicePresident
            else -> throw IllegalStateException("At most ${numPlayers - 1} players can finish with a joker")
        }
    }

    private fun assignRankWhenTwoPlayersForPosition(position: Int, playerId: DwitchPlayerId): Pair<DwitchPlayerId, DwitchRank> {
        return when (position) {
            1 -> playerId to DwitchRank.President
            2 -> playerId to DwitchRank.Asshole
            else -> throw IllegalStateException("There are only two ranks to assign when number of players is two")
        }
    }

    private fun assignRankWhenThreePlayersForPosition(position: Int, playerId: DwitchPlayerId): Pair<DwitchPlayerId, DwitchRank> {
        return when (position) {
            1 -> playerId to DwitchRank.President
            2 -> playerId to DwitchRank.Neutral
            3 -> playerId to DwitchRank.Asshole
            else -> throw IllegalStateException("There are only three ranks to assign when number of players is three")
        }
    }

    private fun assignRankWhenFourPlayersForPosition(position: Int, playerId: DwitchPlayerId): Pair<DwitchPlayerId, DwitchRank> {
        return when (position) {
            1 -> playerId to DwitchRank.President
            2 -> playerId to DwitchRank.VicePresident
            3 -> playerId to DwitchRank.ViceAsshole
            4 -> playerId to DwitchRank.Asshole
            else -> throw IllegalStateException("There are only four ranks to assign when number of players is four")
        }
    }

    private fun assignRankWhenMoreThanFourPlayersForPosition(
        position: Int,
        playerId: DwitchPlayerId,
        numPlayers: Int
    ): Pair<DwitchPlayerId, DwitchRank> {
        return when (position) {
            1 -> playerId to DwitchRank.President
            2 -> playerId to DwitchRank.VicePresident
            in 3 until numPlayers - 1 -> playerId to DwitchRank.Neutral
            numPlayers - 1 -> playerId to DwitchRank.ViceAsshole
            numPlayers -> playerId to DwitchRank.Asshole
            else -> throw IllegalStateException("There are only $numPlayers ranks to assign when number of players is $numPlayers")
        }
    }
}
