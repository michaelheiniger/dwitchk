package ch.qscqlmpa.dwitchengine.rules

import ch.qscqlmpa.dwitchengine.TestEntityFactory
import ch.qscqlmpa.dwitchengine.model.player.Player
import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId
import ch.qscqlmpa.dwitchengine.model.player.Rank
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.DisplayName
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class PlayingOrderTest {

    @Nested
    @DisplayName("getPlayingOrder")
    inner class GetPlayingOrder {

        private lateinit var host: Player
        private lateinit var guest1: Player
        private lateinit var guest2: Player
        private lateinit var guest3: Player
        private lateinit var guest4: Player
        private lateinit var guest5: Player

        @Test
        fun `Two players`() {
            host = TestEntityFactory.createHostPlayer(rank = Rank.President)
            guest1 = TestEntityFactory.createGuestPlayer1(rank = Rank.Asshole)

            val order = launchTest(host, guest1)

            assertThat(order.size).isEqualTo(2)
            assertThat(order[0]).isEqualTo(guest1.id)
            assertThat(order[1]).isEqualTo(host.id)
        }

        @Test
        fun `Three players`() {
            host = TestEntityFactory.createHostPlayer(rank = Rank.Neutral)
            guest1 = TestEntityFactory.createGuestPlayer1(rank = Rank.Asshole)
            guest2 = TestEntityFactory.createGuestPlayer2(rank = Rank.President)

            val order = launchTest(host, guest1, guest2)

            assertThat(order.size).isEqualTo(3)
            assertThat(order[0]).isEqualTo(guest1.id)
            assertThat(order[1]).isEqualTo(host.id)
            assertThat(order[2]).isEqualTo(guest2.id)
        }

        @Test
        fun `Four players`() {
            host = TestEntityFactory.createHostPlayer(rank = Rank.President)
            guest1 = TestEntityFactory.createGuestPlayer1(rank = Rank.VicePresident)
            guest2 = TestEntityFactory.createGuestPlayer2(rank = Rank.Asshole)
            guest3 = TestEntityFactory.createGuestPlayer3(rank = Rank.ViceAsshole)

            val order = launchTest(host, guest1, guest2, guest3)

            assertThat(order.size).isEqualTo(4)
            assertThat(order[0]).isEqualTo(guest2.id)
            assertThat(order[1]).isEqualTo(guest3.id)
            assertThat(order[2]).isEqualTo(guest1.id)
            assertThat(order[3]).isEqualTo(host.id)
        }

        @Test
        fun `Five players`() {
            host = TestEntityFactory.createHostPlayer(rank = Rank.Neutral)
            guest1 = TestEntityFactory.createGuestPlayer1(rank = Rank.President)
            guest2 = TestEntityFactory.createGuestPlayer2(rank = Rank.VicePresident)
            guest3 = TestEntityFactory.createGuestPlayer3(rank = Rank.Asshole)
            guest4 = TestEntityFactory.createGuestPlayer4(rank = Rank.ViceAsshole)

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
            host = TestEntityFactory.createHostPlayer(rank = Rank.Neutral)
            guest1 = TestEntityFactory.createGuestPlayer1(rank = Rank.Neutral)
            guest2 = TestEntityFactory.createGuestPlayer2(rank = Rank.President)
            guest3 = TestEntityFactory.createGuestPlayer3(rank = Rank.VicePresident)
            guest4 = TestEntityFactory.createGuestPlayer4(rank = Rank.Asshole)
            guest5 = TestEntityFactory.createGuestPlayer5(rank = Rank.ViceAsshole)

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

        private fun launchTest(vararg players: Player): List<PlayerDwitchId> {
            return PlayingOrder.getPlayingOrder(players.toList())
        }
    }
}
