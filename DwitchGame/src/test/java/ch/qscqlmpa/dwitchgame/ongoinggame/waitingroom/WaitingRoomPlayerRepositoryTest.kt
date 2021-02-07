package ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom

import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId
import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.TestEntityFactory
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import ch.qscqlmpa.dwitchmodel.player.PlayerWr
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
                TestEntityFactory.createHostPlayer(dwitchId = PlayerDwitchId(1), ready = true)
            val playerGuest1 =
                TestEntityFactory.createGuestPlayer1(dwitchId = PlayerDwitchId(2), ready = false)
            val playerGuest2 =
                TestEntityFactory.createGuestPlayer2(dwitchId = PlayerDwitchId(3), ready = true)

            val players = listOf(hostPlayer, playerGuest1, playerGuest2)
            every { mockInGameStore.observePlayersInWaitingRoom() } returns Flowable.just(players)

            repository.observePlayers().test().assertValue(
                listOf(
                    PlayerWr(
                        PlayerDwitchId(1),
                        hostPlayer.name,
                        PlayerRole.HOST,
                        PlayerConnectionState.CONNECTED,
                        true
                    ),
                    PlayerWr(
                        PlayerDwitchId(2),
                        playerGuest1.name,
                        PlayerRole.GUEST,
                        PlayerConnectionState.CONNECTED,
                        false
                    ),
                    PlayerWr(
                        PlayerDwitchId(3),
                        playerGuest2.name,
                        PlayerRole.GUEST,
                        PlayerConnectionState.CONNECTED,
                        true
                    )
                )
            )
        }
    }
}
