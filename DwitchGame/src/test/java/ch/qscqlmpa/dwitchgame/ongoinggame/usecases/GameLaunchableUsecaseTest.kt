package ch.qscqlmpa.dwitchgame.ongoinggame.usecases

import ch.qscqlmpa.dwitchgame.BaseUnitTest
import ch.qscqlmpa.dwitchgame.TestEntityFactory
import ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom.PlayerWrUi
import ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom.WaitingRoomPlayerRepository
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Observable
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class GameLaunchableUsecaseTest : BaseUnitTest() {

    private val mockPlayerWrRepository = mockk<WaitingRoomPlayerRepository>(relaxed = true)

    private lateinit var gameLaunchableUsecase: GameLaunchableUsecase

    @BeforeEach
    fun setup() {
        gameLaunchableUsecase = GameLaunchableUsecase(mockPlayerWrRepository)
    }

    @Nested
    inner class ObserveDwitchGameEvent {

        @Test
        fun `Send GameIsReadyToBeLaunched when all players are ready`() {
            val players = listOf(TestEntityFactory.createPlayerWrUi1(), TestEntityFactory.createPlayerWrUi2())
            setupPlayerWrListMock(players)

            gameLaunchableUsecase.gameCanBeLaunched().test().assertValue(GameLaunchableEvent.GameIsReadyToBeLaunched)

            verifyMock()
        }

        @Test
        fun `Send NotAllPlayersAreReady when not all players are ready`() {
            val players = listOf(TestEntityFactory.createPlayerWrUi1(), TestEntityFactory.createPlayerWrUi2().copy(ready = false))
            setupPlayerWrListMock(players)

            gameLaunchableUsecase.gameCanBeLaunched().test().assertValue(GameLaunchableEvent.NotAllPlayersAreReady)

            verifyMock()
        }

        @Test
        fun `Send NotEnoughPlayers when not enough players in game`() {
            val players = listOf(TestEntityFactory.createPlayerWrUi1())
            setupPlayerWrListMock(players)

            gameLaunchableUsecase.gameCanBeLaunched().test().assertValue(GameLaunchableEvent.NotEnoughPlayers)

            verifyMock()
        }

        private fun verifyMock() {
            verify { mockPlayerWrRepository.observePlayers() }
            confirmVerified(mockPlayerWrRepository)
        }

        private fun setupPlayerWrListMock(listToReturn: List<PlayerWrUi>) {
            every { mockPlayerWrRepository.observePlayers() } returns Observable.just(listToReturn)
        }
    }
}
