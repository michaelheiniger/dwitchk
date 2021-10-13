package ch.qscqlmpa.dwitchgame.ingame.waitingroom

import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.TestEntityFactory
import ch.qscqlmpa.dwitchgame.ingame.communication.CommunicationStateRepository
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Observable
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class WaitingRoomPlayerRepositoryTest : BaseUnitTest() {

    private val communicationStateRepository = mockk<CommunicationStateRepository>(relaxed = true)

    private lateinit var repository: WaitingRoomPlayerRepository

    @Nested
    inner class ObservePlayers {

        private val host = TestEntityFactory.createHostPlayer()
        private val guest1 = TestEntityFactory.createGuestPlayer1(
            connected = true,
            ready = true,
            computerManaged = true
        )
        private val guest2 = TestEntityFactory.createGuestPlayer2(
            connected = false,
            ready = false,
            computerManaged = false
        )

        @BeforeEach
        fun setup() {
            every { mockInGameStore.observePlayersInWaitingRoom() } returns Observable.just(listOf(host, guest1, guest2))
            every { mockInGameStore.gameIsNew() } returns true
            every { communicationStateRepository.connectedToGame() } returns Observable.just(true)
        }

        @Test
        fun `should return players from store`() {
            createRepository(PlayerRole.HOST)
            every { mockInGameStore.gameIsNew() } returns true

            val testObserver = repository.observePlayers().test()
            testObserver.assertValueCount(1)
            val values = testObserver.values()

            assertThat(values[0].size).isEqualTo(3)

            assertThat(values[0][0].id).isEqualTo(10L)
            assertThat(values[0][0].name).isEqualTo("Aragorn")
            assertThat(values[0][0].connected).isTrue
            assertThat(values[0][0].ready).isTrue

            assertThat(values[0][1].id).isEqualTo(11L)
            assertThat(values[0][1].name).isEqualTo("Boromir")
            assertThat(values[0][1].connected).isTrue
            assertThat(values[0][1].ready).isTrue

            assertThat(values[0][2].id).isEqualTo(12L)
            assertThat(values[0][2].name).isEqualTo("Celeborn")
            assertThat(values[0][2].connected).isFalse
            assertThat(values[0][2].ready).isFalse
        }

        @Test
        fun `when local player is host and game is new, all players are kickable except the host`() {
            createRepository(PlayerRole.HOST)
            every { mockInGameStore.gameIsNew() } returns true

            val values = repository.observePlayers().test().values()

            assertThat(values[0][0].name).isEqualTo(host.name)
            assertThat(values[0][0].kickable).isFalse

            assertThat(values[0][1].name).isEqualTo(guest1.name)
            assertThat(values[0][1].kickable).isTrue

            assertThat(values[0][2].name).isEqualTo(guest2.name)
            assertThat(values[0][2].kickable).isTrue
        }

        @Test
        fun `when local player is host and game is resumed, no player is kickable`() {
            createRepository(PlayerRole.HOST)
            every { mockInGameStore.gameIsNew() } returns false

            val values = repository.observePlayers().test().values()

            assertThat(values[0][0].name).isEqualTo(host.name)
            assertThat(values[0][0].kickable).isFalse

            assertThat(values[0][1].name).isEqualTo(guest1.name)
            assertThat(values[0][1].kickable).isFalse

            assertThat(values[0][2].name).isEqualTo(guest2.name)
            assertThat(values[0][2].kickable).isFalse
        }

        @Test
        fun `when local player is guest and game is new, no player is kickable`() {
            createRepository(PlayerRole.GUEST)
            every { mockInGameStore.gameIsNew() } returns true

            val values = repository.observePlayers().test().values()

            assertThat(values[0][0].name).isEqualTo(host.name)
            assertThat(values[0][0].kickable).isFalse

            assertThat(values[0][1].name).isEqualTo(guest1.name)
            assertThat(values[0][1].kickable).isFalse

            assertThat(values[0][2].name).isEqualTo(guest2.name)
            assertThat(values[0][2].kickable).isFalse
        }

        @Test
        fun `when local player is guest and game is resumed, no player is kickable`() {
            createRepository(PlayerRole.GUEST)
            every { mockInGameStore.gameIsNew() } returns false

            val values = repository.observePlayers().test().values()

            assertThat(values[0][0].name).isEqualTo(host.name)
            assertThat(values[0][0].kickable).isFalse

            assertThat(values[0][1].name).isEqualTo(guest1.name)
            assertThat(values[0][1].kickable).isFalse

            assertThat(values[0][2].name).isEqualTo(guest2.name)
            assertThat(values[0][2].kickable).isFalse
        }

        @Test
        fun `when local player is disconnected, then all players are seen as disconnected - guest`() {
            createRepository(PlayerRole.GUEST)
            every { communicationStateRepository.connectedToGame() } returns Observable.just(false)

            val values = repository.observePlayers().test().values()

            assertThat(values[0][0].name).isEqualTo(host.name)
            assertThat(values[0][0].connected).isFalse

            assertThat(values[0][1].name).isEqualTo(guest1.name)
            assertThat(values[0][1].connected).isFalse

            assertThat(values[0][2].name).isEqualTo(guest2.name)
            assertThat(values[0][2].connected).isFalse
        }

        @Test
        fun `when local player is disconnected, then all players are seen as disconnected - host`() {
            createRepository(PlayerRole.HOST)
            every { communicationStateRepository.connectedToGame() } returns Observable.just(false)

            val values = repository.observePlayers().test().values()

            assertThat(values[0][0].name).isEqualTo(host.name)
            assertThat(values[0][0].connected).isFalse

            assertThat(values[0][1].name).isEqualTo(guest1.name)
            assertThat(values[0][1].connected).isFalse

            assertThat(values[0][2].name).isEqualTo(guest2.name)
            assertThat(values[0][2].connected).isFalse
        }
    }

    private fun createRepository(localPlayerRole: PlayerRole) {
        repository = WaitingRoomPlayerRepository(mockInGameStore, localPlayerRole, communicationStateRepository)
    }
}
