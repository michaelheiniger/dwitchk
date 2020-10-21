package ch.qscqlmpa.dwitch.ongoinggame.waitingroom

import ch.qscqlmpa.dwitch.BaseUnitTest
import ch.qscqlmpa.dwitch.game.TestEntityFactory
import ch.qscqlmpa.dwitch.ongoinggame.communication.waitingroom.PlayerWr
import ch.qscqlmpa.dwitch.ongoinggame.communication.waitingroom.PlayerWrRepository
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import io.mockk.every
import io.reactivex.Flowable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

class PlayerWrRepositoryTest : BaseUnitTest() {

    private lateinit var repository: PlayerWrRepository

    @BeforeEach
    override fun setup() {
        super.setup()
        repository = PlayerWrRepository(mockInGameStore)
    }

    @Nested
    inner class ObserveConnectedPlayers {

        @Test
        fun `should return players from store`() {

            val hostPlayer = TestEntityFactory.createHostPlayer(inGameId = PlayerInGameId(1), ready = true)
            val playerGuest1 = TestEntityFactory.createGuestPlayer1(inGameId = PlayerInGameId(2), ready = false)
            val playerGuest2 = TestEntityFactory.createGuestPlayer2(inGameId = PlayerInGameId(3), ready = true)

            val players = listOf(hostPlayer, playerGuest1, playerGuest2)
            every { mockInGameStore.observeConnectedPlayers() } returns Flowable.just(players)

            repository.observeConnectedPlayers().test().assertValue(listOf(
                    PlayerWr(PlayerInGameId(1), hostPlayer.name, true),
                    PlayerWr(PlayerInGameId(2), playerGuest1.name, false),
                    PlayerWr(PlayerInGameId(3), playerGuest2.name, true)
            ))
        }
    }
}