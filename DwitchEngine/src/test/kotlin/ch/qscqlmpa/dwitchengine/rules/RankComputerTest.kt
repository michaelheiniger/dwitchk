package ch.qscqlmpa.dwitchengine.rules

import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank
import ch.qscqlmpa.dwitchengine.model.player.SpecialRuleBreaker
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class RankComputerTest {

    @Nested
    @DisplayName("computePlayersRank")
    inner class ComputePlayersRank {

        private val player1Id = DwitchPlayerId(1)
        private val player2Id = DwitchPlayerId(2)
        private val player3Id = DwitchPlayerId(3)
        private val player4Id = DwitchPlayerId(4)
        private val player5Id = DwitchPlayerId(5)
        private val player6Id = DwitchPlayerId(6)

        @Test
        fun `Two players`() {
            val list = listOf(player1Id, player2Id)

            val ranks = launchTest(list)

            assertThat(ranks.size).isEqualTo(2)
            assertThat(ranks[player1Id]).isEqualTo(DwitchRank.President)
            assertThat(ranks[player2Id]).isEqualTo(DwitchRank.Asshole)
        }

        @Test
        fun `Two players, one played on first jack`() {
            val list = listOf(player1Id, player2Id) // Player1 finished first ...
            val specialRuleBreakers =
                listOf(SpecialRuleBreaker.PlayedOnFirstJack(player1Id)) // but played on first Jack of the round

            val ranks = launchTest(list, specialRuleBreakers)

            assertThat(ranks.size).isEqualTo(2)
            assertThat(ranks[player1Id]).isEqualTo(DwitchRank.Asshole)
            assertThat(ranks[player2Id]).isEqualTo(DwitchRank.President)
        }

        @Test
        fun `Two players, one finished with joker`() {
            val list = listOf(player1Id, player2Id) // Player1 finished first ...
            val specialRuleBreakers = listOf(SpecialRuleBreaker.FinishWithJoker(player1Id)) // but finished with joker !

            val ranks = launchTest(list, specialRuleBreakers)

            assertThat(ranks.size).isEqualTo(2)
            assertThat(ranks[player1Id]).isEqualTo(DwitchRank.Asshole)
            assertThat(ranks[player2Id]).isEqualTo(DwitchRank.President)
        }

        @Test
        fun `Three players`() {
            val list = listOf(player1Id, player2Id, player3Id)

            val ranks = launchTest(list)

            assertThat(ranks.size).isEqualTo(3)
            assertThat(ranks[player1Id]).isEqualTo(DwitchRank.President)
            assertThat(ranks[player2Id]).isEqualTo(DwitchRank.Neutral)
            assertThat(ranks[player3Id]).isEqualTo(DwitchRank.Asshole)
        }

        @Test
        fun `Three players, one played on first jack of round`() {
            val list = listOf(player1Id, player2Id, player3Id) // Player1 finished first
            val specialRuleBreakers = listOf(SpecialRuleBreaker.PlayedOnFirstJack(player1Id)) // but player on first Jack

            val ranks = launchTest(list, specialRuleBreakers)

            assertThat(ranks.size).isEqualTo(3)
            assertThat(ranks[player1Id]).isEqualTo(DwitchRank.Asshole)
            assertThat(ranks[player2Id]).isEqualTo(DwitchRank.President)
            assertThat(ranks[player3Id]).isEqualTo(DwitchRank.Neutral)
        }

        @Test
        fun `Three players, one finished with joker`() {
            val list = listOf(player1Id, player2Id, player3Id)
            val specialRuleBreakers = listOf(SpecialRuleBreaker.FinishWithJoker(player1Id))

            val ranks = launchTest(list, specialRuleBreakers)

            assertThat(ranks.size).isEqualTo(3)
            assertThat(ranks[player1Id]).isEqualTo(DwitchRank.Asshole)
            assertThat(ranks[player2Id]).isEqualTo(DwitchRank.President)
            assertThat(ranks[player3Id]).isEqualTo(DwitchRank.Neutral)
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
            assertThat(ranks[player1Id]).isEqualTo(DwitchRank.Neutral)
            assertThat(ranks[player2Id]).isEqualTo(DwitchRank.Asshole)
            assertThat(ranks[player3Id]).isEqualTo(DwitchRank.President)
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
            assertThat(ranks[player1Id]).isEqualTo(DwitchRank.Asshole)
            assertThat(ranks[player2Id]).isEqualTo(DwitchRank.Neutral)
            assertThat(ranks[player3Id]).isEqualTo(DwitchRank.President)
        }

        @Test
        fun `Four players`() {
            val list = listOf(player1Id, player2Id, player3Id, player4Id)

            val ranks = launchTest(list)

            assertThat(ranks.size).isEqualTo(4)
            assertThat(ranks[player1Id]).isEqualTo(DwitchRank.President)
            assertThat(ranks[player2Id]).isEqualTo(DwitchRank.VicePresident)
            assertThat(ranks[player3Id]).isEqualTo(DwitchRank.ViceAsshole)
            assertThat(ranks[player4Id]).isEqualTo(DwitchRank.Asshole)
        }

        @Test
        fun `Four players, one finished with joker`() {
            val list = listOf(player1Id, player2Id, player3Id, player4Id)
            val specialRuleBreakers = listOf(SpecialRuleBreaker.FinishWithJoker(player1Id))

            val ranks = launchTest(list, specialRuleBreakers)

            assertThat(ranks.size).isEqualTo(4)
            assertThat(ranks[player1Id]).isEqualTo(DwitchRank.Asshole)
            assertThat(ranks[player2Id]).isEqualTo(DwitchRank.President)
            assertThat(ranks[player3Id]).isEqualTo(DwitchRank.VicePresident)
            assertThat(ranks[player4Id]).isEqualTo(DwitchRank.ViceAsshole)
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
            assertThat(ranks[player1Id]).isEqualTo(DwitchRank.ViceAsshole)
            assertThat(ranks[player2Id]).isEqualTo(DwitchRank.President)
            assertThat(ranks[player3Id]).isEqualTo(DwitchRank.Asshole)
            assertThat(ranks[player4Id]).isEqualTo(DwitchRank.VicePresident)
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
            assertThat(ranks[player1Id]).isEqualTo(DwitchRank.VicePresident)
            assertThat(ranks[player2Id]).isEqualTo(DwitchRank.ViceAsshole)
            assertThat(ranks[player3Id]).isEqualTo(DwitchRank.Asshole)
            assertThat(ranks[player4Id]).isEqualTo(DwitchRank.President)
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
            assertThat(ranks[player1Id]).isEqualTo(DwitchRank.President)
            assertThat(ranks[player2Id]).isEqualTo(DwitchRank.ViceAsshole)
            assertThat(ranks[player3Id]).isEqualTo(DwitchRank.Asshole)
            assertThat(ranks[player4Id]).isEqualTo(DwitchRank.VicePresident)
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
            assertThat(ranks[player1Id]).isEqualTo(DwitchRank.President)
            assertThat(ranks[player2Id]).isEqualTo(DwitchRank.Asshole)
            assertThat(ranks[player3Id]).isEqualTo(DwitchRank.ViceAsshole)
            assertThat(ranks[player4Id]).isEqualTo(DwitchRank.VicePresident)
        }

        @Test
        fun `Five players`() {
            val list = listOf(player1Id, player2Id, player3Id, player4Id, player5Id)

            val ranks = launchTest(list)

            assertThat(ranks.size).isEqualTo(5)
            assertThat(ranks[player1Id]).isEqualTo(DwitchRank.President)
            assertThat(ranks[player2Id]).isEqualTo(DwitchRank.VicePresident)
            assertThat(ranks[player3Id]).isEqualTo(DwitchRank.Neutral)
            assertThat(ranks[player4Id]).isEqualTo(DwitchRank.ViceAsshole)
            assertThat(ranks[player5Id]).isEqualTo(DwitchRank.Asshole)
        }

        @Test
        fun `Five players, one finishes with joker`() {
            val list = listOf(player1Id, player2Id, player3Id, player4Id, player5Id)
            val specialRuleBreakers = listOf(SpecialRuleBreaker.FinishWithJoker(player1Id))

            val ranks = launchTest(list, specialRuleBreakers)

            assertThat(ranks.size).isEqualTo(5)
            assertThat(ranks[player1Id]).isEqualTo(DwitchRank.Asshole)
            assertThat(ranks[player2Id]).isEqualTo(DwitchRank.President)
            assertThat(ranks[player3Id]).isEqualTo(DwitchRank.VicePresident)
            assertThat(ranks[player4Id]).isEqualTo(DwitchRank.Neutral)
            assertThat(ranks[player5Id]).isEqualTo(DwitchRank.ViceAsshole)
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
            assertThat(ranks[player1Id]).isEqualTo(DwitchRank.ViceAsshole)
            assertThat(ranks[player2Id]).isEqualTo(DwitchRank.President)
            assertThat(ranks[player3Id]).isEqualTo(DwitchRank.VicePresident)
            assertThat(ranks[player4Id]).isEqualTo(DwitchRank.Asshole)
            assertThat(ranks[player5Id]).isEqualTo(DwitchRank.Neutral)
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
            assertThat(ranks[player1Id]).isEqualTo(DwitchRank.Neutral)
            assertThat(ranks[player2Id]).isEqualTo(DwitchRank.ViceAsshole)
            assertThat(ranks[player3Id]).isEqualTo(DwitchRank.President)
            assertThat(ranks[player4Id]).isEqualTo(DwitchRank.Asshole)
            assertThat(ranks[player5Id]).isEqualTo(DwitchRank.VicePresident)
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
            assertThat(ranks[player1Id]).isEqualTo(DwitchRank.VicePresident)
            assertThat(ranks[player2Id]).isEqualTo(DwitchRank.Neutral)
            assertThat(ranks[player3Id]).isEqualTo(DwitchRank.ViceAsshole)
            assertThat(ranks[player4Id]).isEqualTo(DwitchRank.Asshole)
            assertThat(ranks[player5Id]).isEqualTo(DwitchRank.President)
        }

        @Test
        fun `Six players`() {
            val list = listOf(player1Id, player2Id, player3Id, player4Id, player5Id, player6Id)

            val ranks = launchTest(list)

            assertThat(ranks.size).isEqualTo(6)
            assertThat(ranks[player1Id]).isEqualTo(DwitchRank.President)
            assertThat(ranks[player2Id]).isEqualTo(DwitchRank.VicePresident)
            assertThat(ranks[player3Id]).isEqualTo(DwitchRank.Neutral)
            assertThat(ranks[player4Id]).isEqualTo(DwitchRank.Neutral)
            assertThat(ranks[player5Id]).isEqualTo(DwitchRank.ViceAsshole)
            assertThat(ranks[player6Id]).isEqualTo(DwitchRank.Asshole)
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
            assertThat(ranks[player1Id]).isEqualTo(DwitchRank.Asshole)
            assertThat(ranks[player2Id]).isEqualTo(DwitchRank.ViceAsshole)
            assertThat(ranks[player3Id]).isEqualTo(DwitchRank.President)
            assertThat(ranks[player4Id]).isEqualTo(DwitchRank.VicePresident)
            assertThat(ranks[player5Id]).isEqualTo(DwitchRank.Neutral)
            assertThat(ranks[player6Id]).isEqualTo(DwitchRank.Neutral)
        }
    }

    private fun launchTest(
        list: List<DwitchPlayerId>,
        specialRuleBreakers: List<SpecialRuleBreaker> = emptyList()
    ): Map<DwitchPlayerId, DwitchRank> {
        return RankComputer.computePlayersRank(list, specialRuleBreakers)
    }
}
