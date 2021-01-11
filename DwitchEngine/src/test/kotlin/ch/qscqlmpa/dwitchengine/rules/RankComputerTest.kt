package ch.qscqlmpa.dwitchengine.rules

import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId
import ch.qscqlmpa.dwitchengine.model.player.Rank
import ch.qscqlmpa.dwitchengine.model.player.SpecialRuleBreaker
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
            val list = listOf(player1Id, player2Id)

            val ranks = launchTest(list)

            assertThat(ranks.size).isEqualTo(2)
            assertThat(ranks[player1Id]).isEqualTo(Rank.President)
            assertThat(ranks[player2Id]).isEqualTo(Rank.Asshole)
        }

        @Test
        fun `Two players, one played on first jack`() {
            val list = listOf(player1Id, player2Id) // Player1 finished first ...
            val specialRuleBreakers = listOf(SpecialRuleBreaker.PlayedOnFirstJack(player1Id)) // but played on first Jack of the round

            val ranks = launchTest(list, specialRuleBreakers)

            assertThat(ranks.size).isEqualTo(2)
            assertThat(ranks[player1Id]).isEqualTo(Rank.Asshole)
            assertThat(ranks[player2Id]).isEqualTo(Rank.President)
        }

        @Test
        fun `Two players, one finished with joker`() {
            val list = listOf(player1Id, player2Id) // Player1 finished first ...
            val specialRuleBreakers = listOf(SpecialRuleBreaker.FinishWithJoker(player1Id)) // but finished with joker !

            val ranks = launchTest(list, specialRuleBreakers)

            assertThat(ranks.size).isEqualTo(2)
            assertThat(ranks[player1Id]).isEqualTo(Rank.Asshole)
            assertThat(ranks[player2Id]).isEqualTo(Rank.President)
        }

        @Test
        fun `Three players`() {
            val list = listOf(player1Id, player2Id, player3Id)

            val ranks = launchTest(list)

            assertThat(ranks.size).isEqualTo(3)
            assertThat(ranks[player1Id]).isEqualTo(Rank.President)
            assertThat(ranks[player2Id]).isEqualTo(Rank.Neutral)
            assertThat(ranks[player3Id]).isEqualTo(Rank.Asshole)
        }

        @Test
        fun `Three players, one played on first jack of round`() {
            val list = listOf(player1Id, player2Id, player3Id) // Player1 finished first
            val specialRuleBreakers = listOf(SpecialRuleBreaker.PlayedOnFirstJack(player1Id)) // but player on first Jack

            val ranks = launchTest(list, specialRuleBreakers)

            assertThat(ranks.size).isEqualTo(3)
            assertThat(ranks[player1Id]).isEqualTo(Rank.Asshole)
            assertThat(ranks[player2Id]).isEqualTo(Rank.President)
            assertThat(ranks[player3Id]).isEqualTo(Rank.Neutral)
        }

        @Test
        fun `Three players, one finished with joker`() {
            val list = listOf(player1Id, player2Id, player3Id)
            val specialRuleBreakers = listOf(SpecialRuleBreaker.FinishWithJoker(player1Id))

            val ranks = launchTest(list, specialRuleBreakers)

            assertThat(ranks.size).isEqualTo(3)
            assertThat(ranks[player1Id]).isEqualTo(Rank.Asshole)
            assertThat(ranks[player2Id]).isEqualTo(Rank.President)
            assertThat(ranks[player3Id]).isEqualTo(Rank.Neutral)
        }

        @Test
        fun `Three players, two finished with joker`() {
            val list = listOf(player1Id, player2Id, player3Id)
            val specialRuleBreakers = listOf(
                SpecialRuleBreaker.FinishWithJoker(player1Id),
                SpecialRuleBreaker.FinishWithJoker(player2Id)
            )

            val ranks = launchTest(list, specialRuleBreakers)

            assertThat(ranks.size).isEqualTo(3)
            assertThat(ranks[player1Id]).isEqualTo(Rank.Neutral)
            assertThat(ranks[player2Id]).isEqualTo(Rank.Asshole)
            assertThat(ranks[player3Id]).isEqualTo(Rank.President)
        }

        @Test
        fun `Three players, two finished with joker - different order`() {
            val list = listOf(player1Id, player2Id, player3Id)
            val specialRuleBreakers = listOf(
                SpecialRuleBreaker.FinishWithJoker(player2Id),
                SpecialRuleBreaker.FinishWithJoker(player1Id)
            )

            val ranks = launchTest(list, specialRuleBreakers)

            assertThat(ranks.size).isEqualTo(3)
            assertThat(ranks[player1Id]).isEqualTo(Rank.Asshole)
            assertThat(ranks[player2Id]).isEqualTo(Rank.Neutral)
            assertThat(ranks[player3Id]).isEqualTo(Rank.President)
        }

        @Test
        fun `Four players`() {
            val list = listOf(player1Id, player2Id, player3Id, player4Id)

            val ranks = launchTest(list)

            assertThat(ranks.size).isEqualTo(4)
            assertThat(ranks[player1Id]).isEqualTo(Rank.President)
            assertThat(ranks[player2Id]).isEqualTo(Rank.VicePresident)
            assertThat(ranks[player3Id]).isEqualTo(Rank.ViceAsshole)
            assertThat(ranks[player4Id]).isEqualTo(Rank.Asshole)
        }

        @Test
        fun `Four players, one finished with joker`() {
            val list = listOf(player1Id, player2Id, player3Id, player4Id)
            val specialRuleBreakers = listOf(SpecialRuleBreaker.FinishWithJoker(player1Id))

            val ranks = launchTest(list, specialRuleBreakers)

            assertThat(ranks.size).isEqualTo(4)
            assertThat(ranks[player1Id]).isEqualTo(Rank.Asshole)
            assertThat(ranks[player2Id]).isEqualTo(Rank.President)
            assertThat(ranks[player3Id]).isEqualTo(Rank.VicePresident)
            assertThat(ranks[player4Id]).isEqualTo(Rank.ViceAsshole)
        }

        @Test
        fun `Four players, two finished with joker`() {
            val list = listOf(player1Id, player2Id, player3Id, player4Id)
            val specialRuleBreakers = listOf(
                SpecialRuleBreaker.FinishWithJoker(player1Id),
                SpecialRuleBreaker.FinishWithJoker(player3Id)
            )

            val ranks = launchTest(list, specialRuleBreakers)

            assertThat(ranks.size).isEqualTo(4)
            assertThat(ranks[player1Id]).isEqualTo(Rank.ViceAsshole)
            assertThat(ranks[player2Id]).isEqualTo(Rank.President)
            assertThat(ranks[player3Id]).isEqualTo(Rank.Asshole)
            assertThat(ranks[player4Id]).isEqualTo(Rank.VicePresident)
        }

        @Test
        fun `Four players, three finished with joker`() {
            val list = listOf(player1Id, player2Id, player3Id, player4Id)
            val specialRuleBreakers = listOf(
                SpecialRuleBreaker.FinishWithJoker(player1Id),
                SpecialRuleBreaker.FinishWithJoker(player2Id),
                SpecialRuleBreaker.FinishWithJoker(player3Id)
            )

            val ranks = launchTest(list, specialRuleBreakers)

            assertThat(ranks.size).isEqualTo(4)
            assertThat(ranks[player1Id]).isEqualTo(Rank.VicePresident)
            assertThat(ranks[player2Id]).isEqualTo(Rank.ViceAsshole)
            assertThat(ranks[player3Id]).isEqualTo(Rank.Asshole)
            assertThat(ranks[player4Id]).isEqualTo(Rank.President)
        }

        @Test
        fun `Four players, one finished with joker and one played on first jack`() {
            val list = listOf(player1Id, player2Id, player3Id, player4Id)
            val specialRuleBreakers = listOf(
                SpecialRuleBreaker.PlayedOnFirstJack(player2Id),
                SpecialRuleBreaker.FinishWithJoker(player3Id)
            )

            val ranks = launchTest(list, specialRuleBreakers)

            assertThat(ranks.size).isEqualTo(4)
            assertThat(ranks[player1Id]).isEqualTo(Rank.President)
            assertThat(ranks[player2Id]).isEqualTo(Rank.ViceAsshole)
            assertThat(ranks[player3Id]).isEqualTo(Rank.Asshole)
            assertThat(ranks[player4Id]).isEqualTo(Rank.VicePresident)
        }

        @Test
        fun `Four players, one finished with joker and one played on first jack - different order`() {
            val list = listOf(player1Id, player2Id, player3Id, player4Id)
            val specialRuleBreakers = listOf(
                SpecialRuleBreaker.FinishWithJoker(player3Id),
                SpecialRuleBreaker.PlayedOnFirstJack(player2Id)
            )

            val ranks = launchTest(list, specialRuleBreakers)

            assertThat(ranks.size).isEqualTo(4)
            assertThat(ranks[player1Id]).isEqualTo(Rank.President)
            assertThat(ranks[player2Id]).isEqualTo(Rank.Asshole)
            assertThat(ranks[player3Id]).isEqualTo(Rank.ViceAsshole)
            assertThat(ranks[player4Id]).isEqualTo(Rank.VicePresident)
        }

        @Test
        fun `Five players`() {
            val list = listOf(player1Id, player2Id, player3Id, player4Id, player5Id)

            val ranks = launchTest(list)

            assertThat(ranks.size).isEqualTo(5)
            assertThat(ranks[player1Id]).isEqualTo(Rank.President)
            assertThat(ranks[player2Id]).isEqualTo(Rank.VicePresident)
            assertThat(ranks[player3Id]).isEqualTo(Rank.Neutral)
            assertThat(ranks[player4Id]).isEqualTo(Rank.ViceAsshole)
            assertThat(ranks[player5Id]).isEqualTo(Rank.Asshole)
        }

        @Test
        fun `Five players, one finishes with joker`() {
            val list = listOf(player1Id, player2Id, player3Id, player4Id, player5Id)
            val specialRuleBreakers = listOf(SpecialRuleBreaker.FinishWithJoker(player1Id))

            val ranks = launchTest(list, specialRuleBreakers)

            assertThat(ranks.size).isEqualTo(5)
            assertThat(ranks[player1Id]).isEqualTo(Rank.Asshole)
            assertThat(ranks[player2Id]).isEqualTo(Rank.President)
            assertThat(ranks[player3Id]).isEqualTo(Rank.VicePresident)
            assertThat(ranks[player4Id]).isEqualTo(Rank.Neutral)
            assertThat(ranks[player5Id]).isEqualTo(Rank.ViceAsshole)
        }

        @Test
        fun `Five players, two finish with joker`() {
            val list = listOf(player1Id, player2Id, player3Id, player4Id, player5Id)
            val specialRuleBreakers = listOf(
                SpecialRuleBreaker.FinishWithJoker(player1Id),
                SpecialRuleBreaker.FinishWithJoker(player4Id)
            )

            val ranks = launchTest(list, specialRuleBreakers)

            assertThat(ranks.size).isEqualTo(5)
            assertThat(ranks[player1Id]).isEqualTo(Rank.ViceAsshole)
            assertThat(ranks[player2Id]).isEqualTo(Rank.President)
            assertThat(ranks[player3Id]).isEqualTo(Rank.VicePresident)
            assertThat(ranks[player4Id]).isEqualTo(Rank.Asshole)
            assertThat(ranks[player5Id]).isEqualTo(Rank.Neutral)
        }

        @Test
        fun `Five players, three finish with joker`() {
            val list = listOf(player1Id, player2Id, player3Id, player4Id, player5Id)
            val specialRuleBreakers = listOf(
                SpecialRuleBreaker.FinishWithJoker(player1Id),
                SpecialRuleBreaker.FinishWithJoker(player2Id),
                SpecialRuleBreaker.FinishWithJoker(player4Id)
            )

            val ranks = launchTest(list, specialRuleBreakers)

            assertThat(ranks.size).isEqualTo(5)
            assertThat(ranks[player1Id]).isEqualTo(Rank.Neutral)
            assertThat(ranks[player2Id]).isEqualTo(Rank.ViceAsshole)
            assertThat(ranks[player3Id]).isEqualTo(Rank.President)
            assertThat(ranks[player4Id]).isEqualTo(Rank.Asshole)
            assertThat(ranks[player5Id]).isEqualTo(Rank.VicePresident)
        }

        @Test
        fun `Five players, four finish with joker`() {
            val list = listOf(player1Id, player2Id, player3Id, player4Id, player5Id)
            val specialRuleBreakers = listOf(
                SpecialRuleBreaker.FinishWithJoker(player1Id),
                SpecialRuleBreaker.FinishWithJoker(player2Id),
                SpecialRuleBreaker.FinishWithJoker(player3Id),
                SpecialRuleBreaker.FinishWithJoker(player4Id)
            )

            val ranks = launchTest(list, specialRuleBreakers)

            assertThat(ranks.size).isEqualTo(5)
            assertThat(ranks[player1Id]).isEqualTo(Rank.VicePresident)
            assertThat(ranks[player2Id]).isEqualTo(Rank.Neutral)
            assertThat(ranks[player3Id]).isEqualTo(Rank.ViceAsshole)
            assertThat(ranks[player4Id]).isEqualTo(Rank.Asshole)
            assertThat(ranks[player5Id]).isEqualTo(Rank.President)
        }

        @Test
        fun `Six players`() {
            val list = listOf(player1Id, player2Id, player3Id, player4Id, player5Id, player6Id)

            val ranks = launchTest(list)

            assertThat(ranks.size).isEqualTo(6)
            assertThat(ranks[player1Id]).isEqualTo(Rank.President)
            assertThat(ranks[player2Id]).isEqualTo(Rank.VicePresident)
            assertThat(ranks[player3Id]).isEqualTo(Rank.Neutral)
            assertThat(ranks[player4Id]).isEqualTo(Rank.Neutral)
            assertThat(ranks[player5Id]).isEqualTo(Rank.ViceAsshole)
            assertThat(ranks[player6Id]).isEqualTo(Rank.Asshole)
        }

        @Test
        fun `Six players, P1 plays on the first Jack of the round, then P2 finishes with Joker, then P1 finishes with Joker`() {
            val list = listOf(player2Id, player1Id, player3Id, player4Id, player5Id, player6Id)
            val specialRuleBreakers = listOf(
                SpecialRuleBreaker.PlayedOnFirstJack(player1Id),
                SpecialRuleBreaker.FinishWithJoker(player2Id),
                SpecialRuleBreaker.FinishWithJoker(player1Id)
            )

            val ranks = launchTest(list, specialRuleBreakers)

            assertThat(ranks.size).isEqualTo(6)
            assertThat(ranks[player1Id]).isEqualTo(Rank.Asshole)
            assertThat(ranks[player2Id]).isEqualTo(Rank.ViceAsshole)
            assertThat(ranks[player3Id]).isEqualTo(Rank.President)
            assertThat(ranks[player4Id]).isEqualTo(Rank.VicePresident)
            assertThat(ranks[player5Id]).isEqualTo(Rank.Neutral)
            assertThat(ranks[player6Id]).isEqualTo(Rank.Neutral)
        }
    }

    private fun launchTest(list: List<PlayerDwitchId>, specialRuleBreakers: List<SpecialRuleBreaker> = emptyList()): Map<PlayerDwitchId, Rank> {
        return RankComputer.computePlayersRank(list, specialRuleBreakers)
    }
}