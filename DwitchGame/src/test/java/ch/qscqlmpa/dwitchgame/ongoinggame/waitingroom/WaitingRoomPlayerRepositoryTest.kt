package ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom

import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.TestEntityFactory
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import io.mockk.every
import io.reactivex.Flowable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class WaitingRoomPlayerRepositoryTest : BaseUnitTest() {

    private lateinit var repository: WaitingRoomPlayerRepository

    @BeforeEach
    override fun setup() {
        super.setup()
        repository = WaitingRoomPlayerRepository(mockInGameStore)
    }

    @Nested
    inner class ObserveConnectedPlayers {

        @Test
        fun `should return players from store`() {
            val hostPlayer =
                TestEntityFactory.createHostPlayer(inGameId = PlayerInGameId(1), ready = true)
            val playerGuest1 =
                TestEntityFactory.createGuestPlayer1(inGameId = PlayerInGameId(2), ready = false)
            val playerGuest2 =
                TestEntityFactory.createGuestPlayer2(inGameId = PlayerInGameId(3), ready = true)

            val players = listOf(hostPlayer, playerGuest1, playerGuest2)
            every { mockInGameStore.observePlayersInWaitingRoom() } returns Flowable.just(players)

            repository.observePlayers().test().assertValue(
                listOf(
                    PlayerWr(
                        PlayerInGameId(1),
                        hostPlayer.name,
                        true,
                        PlayerConnectionState.CONNECTED
                    ),
                    PlayerWr(
                        PlayerInGameId(2),
                        playerGuest1.name,
                        false,
                        PlayerConnectionState.CONNECTED
                    ),
                    PlayerWr(
                        PlayerInGameId(3),
                        playerGuest2.name,
                        true,
                        PlayerConnectionState.CONNECTED
                    )
                )
            )
        }
    }
}