package ch.qscqlmpa.dwitch.ongoinggame.usecases

import ch.qscqlmpa.dwitch.BaseUnitTest
import ch.qscqlmpa.dwitch.game.TestEntityFactory
import ch.qscqlmpa.dwitch.ongoinggame.communication.waitingroom.PlayerWr
import ch.qscqlmpa.dwitch.ongoinggame.communication.waitingroom.WaitingRoomPlayerRepository
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Observable
import org.junit.jupiter.api.AfterEach
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Nested
import org.junit.jupiter.api.Test

internal class GameLaunchableUsecaseTest : BaseUnitTest() {

    private val mockPlayerWrRepository = mockk<WaitingRoomPlayerRepository>(relaxed = true)

    private lateinit var gameLaunchableUsecase: GameLaunchableUsecase

    @BeforeEach
    override fun setup() {
        super.setup()
        gameLaunchableUsecase = GameLaunchableUsecase(mockPlayerWrRepository)
    }

    @AfterEach
    override fun tearDown() {
        super.tearDown()
    }

    @Nested
    inner class ObserveGameEvent {

        @Test
        fun `send GameIsReadyToBeLaunched when all players are ready`() {
            val players = listOf(TestEntityFactory.createPlayerWr1(), TestEntityFactory.createPlayerWr2())
            setupPlayerWrListMock(players)

            gameLaunchableUsecase.gameCanBeLaunched().test().assertValue(GameLaunchableEvent.GameIsReadyToBeLaunched)

            verifyMock()
        }

        @Test
        fun `send NotAllPlayersAreReady when not all players are ready`() {
            val players = listOf(TestEntityFactory.createPlayerWr1(), TestEntityFactory.createPlayerWr2().copy(ready = false))
            setupPlayerWrListMock(players)

            gameLaunchableUsecase.gameCanBeLaunched().test().assertValue(GameLaunchableEvent.NotAllPlayersAreReady)

            verifyMock()
        }

        @Test
        fun `send NotEnoughPlayers when not enough players in game`() {
            val players = listOf(TestEntityFactory.createPlayerWr1())
            setupPlayerWrListMock(players)

            gameLaunchableUsecase.gameCanBeLaunched().test().assertValue(GameLaunchableEvent.NotEnoughPlayers)

            verifyMock()
        }

        private fun verifyMock() {
            verify { mockPlayerWrRepository.observePlayers() }
            confirmVerified(mockPlayerWrRepository)
        }

        private fun setupPlayerWrListMock(listToReturn: List<PlayerWr>) {
            every { mockPlayerWrRepository.observePlayers() } returns Observable.just(listToReturn)
        }
    }
}