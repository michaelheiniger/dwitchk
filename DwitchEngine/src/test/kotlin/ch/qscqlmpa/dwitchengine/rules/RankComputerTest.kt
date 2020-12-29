package ch.qscqlmpa.dwitchengine.rules

import ch.qscqlmpa.dwitchengine.model.player.PlayerDone
import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId
import ch.qscqlmpa.dwitchengine.model.player.Rank
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class RankComputerTest {

    @Nested
    @DisplayName("computePlayersRank")
    inner class ComputePlayersRank {

        private val player1Id = PlayerDwitchId(1)
        private val player2Id = PlayerDwitchId(2)
        private val player3Id = PlayerDwitchId(3)
        private val player4Id = PlayerDwitchId(4)
        private val player5Id = PlayerDwitchId(5)
        private val player6Id = PlayerDwitchId(6)

        @Test
        fun `Two players`() {
            val list = listOf(PlayerDone(player1Id, false), PlayerDone(player2Id, false))

            val ranks = RankComputer.computePlayersRank(list)

            assertThat(ranks.size).isEqualTo(2)
            assertThat(ranks[player1Id]).isEqualTo(Rank.President)
            assertThat(ranks[player2Id]).isEqualTo(Rank.Asshole)
        }

        @Test
        fun `Two players, one finish with joker`() {
            val list = listOf(PlayerDone(player1Id, true), PlayerDone(player2Id, false))

            val ranks = RankComputer.computePlayersRank(list)

            assertThat(ranks.size).isEqualTo(2)
            assertThat(ranks[player1Id]).isEqualTo(Rank.Asshole)
            assertThat(ranks[player2Id]).isEqualTo(Rank.President)
        }

        @Test
        fun `Three players`() {
            val list = listOf(
                    PlayerDone(player1Id, false),
                    PlayerDone(player2Id, false),
                    PlayerDone(player3Id, false)
            )

            val ranks = RankComputer.computePlayersRank(list)

            assertThat(ranks.size).isEqualTo(3)
            assertThat(ranks[player1Id]).isEqualTo(Rank.President)
            assertThat(ranks[player2Id]).isEqualTo(Rank.Neutral)
            assertThat(ranks[player3Id]).isEqualTo(Rank.Asshole)
        }

        @Test
        fun `Three players, one finishes with joker`() {
            val list = listOf(
                    PlayerDone(player1Id, true),
                    PlayerDone(player2Id, false),
                    PlayerDone(player3Id, false)
            )

            val ranks = RankComputer.computePlayersRank(list)

            assertThat(ranks.size).isEqualTo(3)
            assertThat(ranks[player1Id]).isEqualTo(Rank.Asshole)
            assertThat(ranks[player2Id]).isEqualTo(Rank.President)
            assertThat(ranks[player3Id]).isEqualTo(Rank.Neutral)
        }

        @Test
        fun `Three players, two finish with joker`() {
            val list = listOf(
                    PlayerDone(player1Id, true),
                    PlayerDone(player2Id, true),
                    PlayerDone(player3Id, false)
            )

            val ranks = RankComputer.computePlayersRank(list)

            assertThat(ranks.size).isEqualTo(3)
            assertThat(ranks[player1Id]).isEqualTo(Rank.Neutral)
            assertThat(ranks[player2Id]).isEqualTo(Rank.Asshole)
            assertThat(ranks[player3Id]).isEqualTo(Rank.President)
        }

        @Test
        fun `Four players`() {
            val list = listOf(
                    PlayerDone(player1Id, false),
                    PlayerDone(player2Id, false),
                    PlayerDone(player3Id, false),
                    PlayerDone(player4Id, false)
            )

            val ranks = RankComputer.computePlayersRank(list)

            assertThat(ranks.size).isEqualTo(4)
            assertThat(ranks[player1Id]).isEqualTo(Rank.President)
            assertThat(ranks[player2Id]).isEqualTo(Rank.VicePresident)
            assertThat(ranks[player3Id]).isEqualTo(Rank.ViceAsshole)
            assertThat(ranks[player4Id]).isEqualTo(Rank.Asshole)
        }

        @Test
        fun `Four players, one finishes with joker`() {
            val list = listOf(
                    PlayerDone(player1Id, true),
                    PlayerDone(player2Id, false),
                    PlayerDone(player3Id, false),
                    PlayerDone(player4Id, false)
            )

            val ranks = RankComputer.computePlayersRank(list)

            assertThat(ranks.size).isEqualTo(4)
            assertThat(ranks[player1Id]).isEqualTo(Rank.Asshole)
            assertThat(ranks[player2Id]).isEqualTo(Rank.President)
            assertThat(ranks[player3Id]).isEqualTo(Rank.VicePresident)
            assertThat(ranks[player4Id]).isEqualTo(Rank.ViceAsshole)
        }

        @Test
        fun `Four players, two finish with joker`() {
            val list = listOf(
                    PlayerDone(player1Id, true),
                    PlayerDone(player2Id, false),
                    PlayerDone(player3Id, true),
                    PlayerDone(player4Id, false)
            )

            val ranks = RankComputer.computePlayersRank(list)

            assertThat(ranks.size).isEqualTo(4)
            assertThat(ranks[player1Id]).isEqualTo(Rank.ViceAsshole)
            assertThat(ranks[player2Id]).isEqualTo(Rank.President)
            assertThat(ranks[player3Id]).isEqualTo(Rank.Asshole)
            assertThat(ranks[player4Id]).isEqualTo(Rank.VicePresident)
        }

        @Test
        fun `Four players, three finish with joker`() {
            val list = listOf(
                    PlayerDone(player1Id, true),
                    PlayerDone(player2Id, true),
                    PlayerDone(player3Id, true),
                    PlayerDone(player4Id, false)
            )

            val ranks = RankComputer.computePlayersRank(list)

            assertThat(ranks.size).isEqualTo(4)
            assertThat(ranks[player1Id]).isEqualTo(Rank.VicePresident)
            assertThat(ranks[player2Id]).isEqualTo(Rank.ViceAsshole)
            assertThat(ranks[player3Id]).isEqualTo(Rank.Asshole)
            assertThat(ranks[player4Id]).isEqualTo(Rank.President)
        }

        @Test
        fun `Five players`() {
            val list = listOf(
                    PlayerDone(player1Id, false),
                    PlayerDone(player2Id, false),
                    PlayerDone(player3Id, false),
                    PlayerDone(player4Id, false),
                    PlayerDone(player5Id, false)
            )

            val ranks = RankComputer.computePlayersRank(list)

            assertThat(ranks.size).isEqualTo(5)
            assertThat(ranks[player1Id]).isEqualTo(Rank.President)
            assertThat(ranks[player2Id]).isEqualTo(Rank.VicePresident)
            assertThat(ranks[player3Id]).isEqualTo(Rank.Neutral)
            assertThat(ranks[player4Id]).isEqualTo(Rank.ViceAsshole)
            assertThat(ranks[player5Id]).isEqualTo(Rank.Asshole)
        }

        @Test
        fun `Five players, one finishes with joker`() {
            val list = listOf(
                    PlayerDone(player1Id, true),
                    PlayerDone(player2Id, false),
                    PlayerDone(player3Id, false),
                    PlayerDone(player4Id, false),
                    PlayerDone(player5Id, false)
            )

            val ranks = RankComputer.computePlayersRank(list)

            assertThat(ranks.size).isEqualTo(5)
            assertThat(ranks[player1Id]).isEqualTo(Rank.Asshole)
            assertThat(ranks[player2Id]).isEqualTo(Rank.President)
            assertThat(ranks[player3Id]).isEqualTo(Rank.VicePresident)
            assertThat(ranks[player4Id]).isEqualTo(Rank.Neutral)
            assertThat(ranks[player5Id]).isEqualTo(Rank.ViceAsshole)
        }

        @Test
        fun `Five players, two finish with joker`() {
            val list = listOf(
                    PlayerDone(player1Id, true),
                    PlayerDone(player2Id, false),
                    PlayerDone(player3Id, false),
                    PlayerDone(player4Id, true),
                    PlayerDone(player5Id, false)
            )

            val ranks = RankComputer.computePlayersRank(list)

            assertThat(ranks.size).isEqualTo(5)
            assertThat(ranks[player1Id]).isEqualTo(Rank.ViceAsshole)
            assertThat(ranks[player2Id]).isEqualTo(Rank.President)
            assertThat(ranks[player3Id]).isEqualTo(Rank.VicePresident)
            assertThat(ranks[player4Id]).isEqualTo(Rank.Asshole)
            assertThat(ranks[player5Id]).isEqualTo(Rank.Neutral)
        }

        @Test
        fun `Five players, three finish with joker`() {
            val list = listOf(
                    PlayerDone(player1Id, true),
                    PlayerDone(player2Id, true),
                    PlayerDone(player3Id, false),
                    PlayerDone(player4Id, true),
                    PlayerDone(player5Id, false)
            )

            val ranks = RankComputer.computePlayersRank(list)

            assertThat(ranks.size).isEqualTo(5)
            assertThat(ranks[player1Id]).isEqualTo(Rank.Neutral)
            assertThat(ranks[player2Id]).isEqualTo(Rank.ViceAsshole)
            assertThat(ranks[player3Id]).isEqualTo(Rank.President)
            assertThat(ranks[player4Id]).isEqualTo(Rank.Asshole)
            assertThat(ranks[player5Id]).isEqualTo(Rank.VicePresident)
        }

        @Test
        fun `Five players, four finish with joker`() {
            val list = listOf(
                    PlayerDone(player1Id, true),
                    PlayerDone(player2Id, true),
                    PlayerDone(player3Id, true),
                    PlayerDone(player4Id, true),
                    PlayerDone(player5Id, false)
            )

            val ranks = RankComputer.computePlayersRank(list)

            assertThat(ranks.size).isEqualTo(5)
            assertThat(ranks[player1Id]).isEqualTo(Rank.VicePresident)
            assertThat(ranks[player2Id]).isEqualTo(Rank.Neutral)
            assertThat(ranks[player3Id]).isEqualTo(Rank.ViceAsshole)
            assertThat(ranks[player4Id]).isEqualTo(Rank.Asshole)
            assertThat(ranks[player5Id]).isEqualTo(Rank.President)
        }

        @Test
        fun `Six players`() {
            val list = listOf(
                    PlayerDone(player1Id, false),
                    PlayerDone(player2Id, false),
                    PlayerDone(player3Id, false),
                    PlayerDone(player4Id, false),
                    PlayerDone(player5Id, false),
                    PlayerDone(player6Id, false)
            )

            val ranks = RankComputer.computePlayersRank(list)

            assertThat(ranks.size).isEqualTo(6)
            assertThat(ranks[player1Id]).isEqualTo(Rank.President)
            assertThat(ranks[player2Id]).isEqualTo(Rank.VicePresident)
            assertThat(ranks[player3Id]).isEqualTo(Rank.Neutral)
            assertThat(ranks[player4Id]).isEqualTo(Rank.Neutral)
            assertThat(ranks[player5Id]).isEqualTo(Rank.ViceAsshole)
            assertThat(ranks[player6Id]).isEqualTo(Rank.Asshole)
        }

        @Test
        fun `One player finishes with Joker and becomes the Asshole`() {
            val list = listOf(
                    PlayerDone(player1Id, true),
                    PlayerDone(player2Id, false),
                    PlayerDone(player3Id, false),
                    PlayerDone(player4Id, false),
                    PlayerDone(player5Id, false)
            )

            val ranks = RankComputer.computePlayersRank(list)

            assertThat(ranks.size).isEqualTo(5)
            assertThat(ranks[player1Id]).isEqualTo(Rank.Asshole)
            assertThat(ranks[player2Id]).isEqualTo(Rank.President)
            assertThat(ranks[player3Id]).isEqualTo(Rank.VicePresident)
            assertThat(ranks[player4Id]).isEqualTo(Rank.Neutral)
            assertThat(ranks[player5Id]).isEqualTo(Rank.ViceAsshole)
        }

        @Test
        fun `Two players finishes with Joker, the first player to finish with joker becomes ViceAsshole, the second player to finish with Joker becomes the Asshole`() {
            val list = listOf(
                    PlayerDone(player1Id, true),
                    PlayerDone(player2Id, false),
                    PlayerDone(player3Id, true),
                    PlayerDone(player4Id, false),
                    PlayerDone(player5Id, false)
            )

            val ranks = RankComputer.computePlayersRank(list)

            assertThat(ranks.size).isEqualTo(5)
            assertThat(ranks[player1Id]).isEqualTo(Rank.ViceAsshole)
            assertThat(ranks[player2Id]).isEqualTo(Rank.President)
            assertThat(ranks[player3Id]).isEqualTo(Rank.Asshole)
            assertThat(ranks[player4Id]).isEqualTo(Rank.VicePresident)
            assertThat(ranks[player5Id]).isEqualTo(Rank.Neutral)
        }
    }
}