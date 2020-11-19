package ch.qscqlmpa.dwitch.ui.newgame

import ch.qscqlmpa.dwitch.BaseViewModelUnitTest
import ch.qscqlmpa.dwitch.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitch.home.HomeGuestFacade
import ch.qscqlmpa.dwitch.home.HomeHostFacade
import ch.qscqlmpa.dwitch.model.game.GameCommonId
import ch.qscqlmpa.dwitch.scheduler.TestSchedulerFactory
import ch.qscqlmpa.dwitch.ui.home.newgame.NewGameActivityViewModel
import ch.qscqlmpa.dwitch.ui.home.newgame.NewGameEvent
import ch.qscqlmpa.dwitch.utils.DisposableManager
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.junit.jupiter.api.Assertions.assertEquals

class NewGameActivityViewModelTest : BaseViewModelUnitTest() {

    private val mockHostFacade = mockk<HomeHostFacade>(relaxed = true)
    private val mockGuestFacade = mockk<HomeGuestFacade>(relaxed = true)

    private lateinit var viewModel: NewGameActivityViewModel

    private val playerName = "Bernard"

    @Before
    override fun setup() {
        super.setup()
        viewModel = NewGameActivityViewModel(mockHostFacade, mockGuestFacade, DisposableManager(), TestSchedulerFactory())
    }

    @Test
    fun nextForGuest_success() {
        every { mockGuestFacade.joinGame(any(), any()) } returns Completable.complete()

        val advertisedGame = AdvertisedGame("Dwiiitch !", GameCommonId(1), "192.168.1.1", 8890)

        viewModel.nextForGuest(advertisedGame, playerName)

        assertEquals(NewGameEvent.SETUP_SUCCESSFUL, viewModel.observeEvents().value)
        verify { mockGuestFacade.joinGame(advertisedGame, playerName) }

        confirmVerified(mockGuestFacade)
    }

    @Test
    fun nextForGuest_error() {
        val advertisedGame = AdvertisedGame("", GameCommonId(1), "192.168.1.1", 8890)

        viewModel.nextForGuest(advertisedGame, playerName)

        assertNull(viewModel.observeEvents().value)

        confirmVerified(mockGuestFacade)
    }

    @Test
    fun nextForHost_success() {
        every { mockHostFacade.hostGame(any(), any()) } returns Completable.complete()
        val gameName = "It is ON !"

        viewModel.nextForHost(gameName, playerName)

        assertEquals(NewGameEvent.SETUP_SUCCESSFUL, viewModel.observeEvents().value)
        verify { mockHostFacade.hostGame(gameName, playerName) }

        confirmVerified(mockHostFacade)
    }

    @Test
    fun nextForHost_error() {
        val gameName = "It is ON !"

        viewModel.nextForHost(gameName, "")

        assertNull(viewModel.observeEvents().value)

        confirmVerified(mockHostFacade)
    }
}