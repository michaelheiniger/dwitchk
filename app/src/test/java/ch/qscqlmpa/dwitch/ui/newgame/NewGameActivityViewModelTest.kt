package ch.qscqlmpa.dwitch.ui.newgame

import ch.qscqlmpa.dwitch.BaseViewModelUnitTest
import ch.qscqlmpa.dwitch.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitch.model.game.GameCommonId
import ch.qscqlmpa.dwitch.scheduler.TestSchedulerFactory
import ch.qscqlmpa.dwitch.ui.home.newgame.NewGameActivityViewModel
import ch.qscqlmpa.dwitch.ui.home.newgame.NewGameEvent
import ch.qscqlmpa.dwitch.usecases.NewGameUsecase
import ch.qscqlmpa.dwitch.utils.DisposableManager
import io.mockk.*
import io.reactivex.Completable
import org.junit.After
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals

class NewGameActivityViewModelTest : BaseViewModelUnitTest() {

    private val mockNewGameUsecase = mockk<NewGameUsecase>(relaxed = true)

    private lateinit var viewModel: NewGameActivityViewModel

    private val playerName = "Bernard"

    @Before
    override fun setup() {
        super.setup()
        viewModel = NewGameActivityViewModel(mockNewGameUsecase, DisposableManager(), TestSchedulerFactory())
    }

    @After
    override fun tearDown() {
        super.tearDown()
        clearMocks(mockNewGameUsecase)
    }

    @Test
    fun nextForGuest_success() {

        every { mockNewGameUsecase.joinGame(any(), any()) } returns Completable.complete()

        val advertisedGame = AdvertisedGame("Dwiiitch !", GameCommonId(1), "192.168.1.1", 8890)

        viewModel.nextForGuest(advertisedGame, playerName)

        assertEquals(NewGameEvent.SETUP_SUCCESSFUL, viewModel.observeEvents().value)
        verify { mockNewGameUsecase.joinGame(advertisedGame, playerName) }

        confirmVerified(mockNewGameUsecase)
    }

    @Test
    fun nextForGuest_error() {

        val advertisedGame = AdvertisedGame("", GameCommonId(1), "192.168.1.1", 8890)

        viewModel.nextForGuest(advertisedGame, playerName)

        assertNull(viewModel.observeEvents().value)

        confirmVerified(mockNewGameUsecase)
    }

    @Test
    fun nextForHost_success() {

        every { mockNewGameUsecase.hostNewGame(any(), any()) } returns Completable.complete()

        val gameName = "It is ON !"

        viewModel.nextForHost(gameName, playerName)

        assertEquals(NewGameEvent.SETUP_SUCCESSFUL, viewModel.observeEvents().value)
        verify { mockNewGameUsecase.hostNewGame(gameName, playerName) }

        confirmVerified(mockNewGameUsecase)
    }

    @Test
    fun nextForHost_error() {

        val gameName = "It is ON !"

        viewModel.nextForHost(gameName, "")

        assertNull(viewModel.observeEvents().value)

        confirmVerified(mockNewGameUsecase)
    }
}