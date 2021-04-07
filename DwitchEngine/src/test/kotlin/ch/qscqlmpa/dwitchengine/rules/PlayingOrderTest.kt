package ch.qscqlmpa.dwitchengine.rules

import ch.qscqlmpa.dwitchengine.TestEntityFactory
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayer
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class PlayingOrderTest {

    @Nested
    @DisplayName("getPlayingOrder")
    inner class GetPlayingOrder {

        private lateinit var host: DwitchPlayer
        private lateinit var guest1: DwitchPlayer
        private lateinit var guest2: DwitchPlayer
        private lateinit var guest3: DwitchPlayer
        private lateinit var guest4: DwitchPlayer
        private lateinit var guest5: DwitchPlayer

        @Test
        fun `Two players`() {
            host = TestEntityFactory.createHostPlayer(rank = DwitchRank.President)
            guest1 = TestEntityFactory.createGuestPlayer1(rank = DwitchRank.Asshole)

            val order = launchTest(host, guest1)

            assertThat(order.size).isEqualTo(2)
            assertThat(order[0]).isEqualTo(guest1.id)
            assertThat(order[1]).isEqualTo(host.id)
        }

        @Test
        fun `Three players`() {
            host = TestEntityFactory.createHostPlayer(rank = DwitchRank.Neutral)
            guest1 = TestEntityFactory.createGuestPlayer1(rank = DwitchRank.Asshole)
            guest2 = TestEntityFactory.createGuestPlayer2(rank = DwitchRank.President)

            val order = launchTest(host, guest1, guest2)

            assertThat(order.size).isEqualTo(3)
            assertThat(order[0]).isEqualTo(guest1.id)
            assertThat(order[1]).isEqualTo(host.id)
            assertThat(order[2]).isEqualTo(guest2.id)
        }

        @Test
        fun `Four players`() {
            host = TestEntityFactory.createHostPlayer(rank = DwitchRank.President)
            guest1 = TestEntityFactory.createGuestPlayer1(rank = DwitchRank.VicePresident)
            guest2 = TestEntityFactory.createGuestPlayer2(rank = DwitchRank.Asshole)
            guest3 = TestEntityFactory.createGuestPlayer3(rank = DwitchRank.ViceAsshole)

            val order = launchTest(host, guest1, guest2, guest3)

            assertThat(order.size).isEqualTo(4)
            assertThat(order[0]).isEqualTo(guest2.id)
            assertThat(order[1]).isEqualTo(guest3.id)
            assertThat(order[2]).isEqualTo(guest1.id)
            assertThat(order[3]).isEqualTo(host.id)
        }

        @Test
        fun `Five players`() {
            host = TestEntityFactory.createHostPlayer(rank = DwitchRank.Neutral)
            guest1 = TestEntityFactory.createGuestPlayer1(rank = DwitchRank.President)
            guest2 = TestEntityFactory.createGuestPlayer2(rank = DwitchRank.VicePresident)
            guest3 = TestEntityFactory.createGuestPlayer3(rank = DwitchRank.Asshole)
            guest4 = TestEntityFactory.createGuestPlayer4(rank = DwitchRank.ViceAsshole)

            val order = launchTest(host, guest1, guest2, guest3, guest4)

            assertThat(order.size).isEqualTo(5)
            assertThat(order[0]).isEqualTo(guest3.id)
            assertThat(order[1]).isEqualTo(guest4.id)
            assertThat(order[2]).isEqualTo(host.id)
            assertThat(order[3]).isEqualTo(guest2.id)
            assertThat(order[4]).isEqualTo(guest1.id)
        }

        @Test
        fun `Six players`() {
            host = TestEntityFactory.createHostPlayer(rank = DwitchRank.Neutral)
            guest1 = TestEntityFactory.createGuestPlayer1(rank = DwitchRank.Neutral)
            guest2 = TestEntityFactory.createGuestPlayer2(rank = DwitchRank.President)
            guest3 = TestEntityFactory.createGuestPlayer3(rank = DwitchRank.VicePresident)
            guest4 = TestEntityFactory.createGuestPlayer4(rank = DwitchRank.Asshole)
            guest5 = TestEntityFactory.createGuestPlayer5(rank = DwitchRank.ViceAsshole)

            val order = launchTest(host, guest1, guest2, guest3, guest4, guest5)

            assertThat(order.size).isEqualTo(6)
            assertThat(order[0]).isEqualTo(guest4.id)
            assertThat(order[1]).isEqualTo(guest5.id)

            // When the rank is equal, the order of the provided list is used.
            assertThat(order[2]).isEqualTo(host.id)
            assertThat(order[3]).isEqualTo(guest1.id)

            assertThat(order[4]).isEqualTo(guest3.id)
            assertThat(order[5]).isEqualTo(guest2.id)
        }

        private fun launchTest(vararg players: DwitchPlayer): List<DwitchPlayerId> {
            return PlayingOrder.getPlayingOrder(players.toList())
        }
    }
}
