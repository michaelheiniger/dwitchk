package ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom

import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.TestEntityFactory
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import io.mockk.every
import io.reactivex.rxjava3.core.Flowable
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
                TestEntityFactory.createHostPlayer(dwitchId = DwitchPlayerId(1), ready = true)
            val playerGuest1 =
                TestEntityFactory.createGuestPlayer1(dwitchId = DwitchPlayerId(2), ready = false)
            val playerGuest2 =
                TestEntityFactory.createGuestPlayer2(dwitchId = DwitchPlayerId(3), ready = true)

            val players = listOf(hostPlayer, playerGuest1, playerGuest2)
            every { mockInGameStore.observePlayersInWaitingRoom() } returns Flowable.just(players)

            repository.observePlayers().test().assertValue(
                listOf(
                    PlayerWrUi(
                        hostPlayer.name,
                        PlayerConnectionState.CONNECTED,
                        ready = true
                    ),
                    PlayerWrUi(
                        playerGuest1.name,
                        PlayerConnectionState.CONNECTED,
                        ready = false
                    ),
                    PlayerWrUi(
                        playerGuest2.name,
                        PlayerConnectionState.CONNECTED,
                        ready = true
                    )
                )
            )
        }
    }
}
