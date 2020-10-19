package ch.qscqlmpa.dwitch.ui.newgame

import ch.qscqlmpa.dwitch.BaseViewModelUnitTest
import ch.qscqlmpa.dwitch.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitch.scheduler.TestSchedulerFactory
import ch.qscqlmpa.dwitch.ui.home.newgame.NewGameActivityViewModel
import ch.qscqlmpa.dwitch.ui.home.newgame.NewGameEvent
import ch.qscqlmpa.dwitch.usecases.NewGameUsecase
import ch.qscqlmpa.dwitch.utils.DisposableManager
import io.mockk.*
import io.reactivex.Completable
import io.reactivex.schedulers.TestScheduler
import org.junit.After
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals
import java.util.concurrent.TimeUnit

class NewGameActivityViewModelTest : BaseViewModelUnitTest() {

    private val mockNewGameUsecase = mockk<NewGameUsecase>(relaxed = true)

    private lateinit var viewModel: NewGameActivityViewModel

    private lateinit var timeScheduler: TestScheduler

    private val playerName = "Bernard"

    @Before
    override fun setup() {
        super.setup()
        timeScheduler = TestScheduler()
        val schedulerFactory = TestSchedulerFactory()
        schedulerFactory.setTimeScheduler(timeScheduler)
        viewModel = NewGameActivityViewModel(mockNewGameUsecase, DisposableManager(), schedulerFactory)
    }

    @After
    override fun tearDown() {
        super.tearDown()
        clearMocks(mockNewGameUsecase)
    }

    @Test
    fun nextForGuest_success() {

        every { mockNewGameUsecase.joinGame(any(), any()) } returns Completable.complete()

        val advertisedGame = AdvertisedGame("Dwiiitch !", "192.168.1.1", 8890)

        viewModel.nextForGuest(advertisedGame, playerName)

        timeScheduler.advanceTimeTo(1, TimeUnit.SECONDS)
        assertEquals(NewGameEvent.SETUP_SUCCESSFUL, viewModel.observeEvents().value)
        verify { mockNewGameUsecase.joinGame(advertisedGame, playerName) }

        confirmVerified(mockNewGameUsecase)
    }

    @Test
    fun nextForGuest_error() {

        val advertisedGame = AdvertisedGame("", "192.168.1.1", 8890)

        viewModel.nextForGuest(advertisedGame, playerName)

        assertNull(viewModel.observeEvents().value)

        confirmVerified(mockNewGameUsecase)
    }

    @Test
    fun nextForHost_success() {

        every { mockNewGameUsecase.hostNewgame(any(), any()) } returns Completable.complete()

        val gameName = "It is ON !"

        viewModel.nextForHost(gameName, playerName)

        timeScheduler.advanceTimeTo(1, TimeUnit.SECONDS)
        assertEquals(NewGameEvent.SETUP_SUCCESSFUL, viewModel.observeEvents().value)
        verify { mockNewGameUsecase.hostNewgame(gameName, playerName) }

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