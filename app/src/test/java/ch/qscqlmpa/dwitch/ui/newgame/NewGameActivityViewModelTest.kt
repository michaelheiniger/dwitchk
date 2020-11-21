package ch.qscqlmpa.dwitch.ui.newgame

import ch.qscqlmpa.dwitch.BaseViewModelUnitTest
import ch.qscqlmpa.dwitch.ui.home.newgame.NewGameActivityViewModel
import ch.qscqlmpa.dwitch.ui.home.newgame.NewGameEvent
import ch.qscqlmpa.dwitchcommonutil.scheduler.TestSchedulerFactory
import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitchgame.home.HomeGuestFacade
import ch.qscqlmpa.dwitchgame.home.HomeHostFacade
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
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
    private val gamePort = 8889

    @Before
    override fun setup() {
        super.setup()
        viewModel = NewGameActivityViewModel(mockHostFacade, mockGuestFacade,
            ch.qscqlmpa.dwitchcommonutil.DisposableManager(), TestSchedulerFactory())
    }

    @Test
    fun nextForGuest_success() {
        every { mockGuestFacade.joinGame(any(), any()) } returns Completable.complete()

        val advertisedGame = AdvertisedGame("Dwiiitch !", GameCommonId(1), "192.168.1.1", gamePort)

        viewModel.nextForGuest(advertisedGame, playerName)

        assertEquals(NewGameEvent.SETUP_SUCCESSFUL, viewModel.observeEvents().value)
        verify { mockGuestFacade.joinGame(advertisedGame, playerName) }

        confirmVerified(mockGuestFacade)
    }

    @Test
    fun nextForGuest_error() {
        val advertisedGame = AdvertisedGame("", GameCommonId(1), "192.168.1.1", gamePort)

        viewModel.nextForGuest(advertisedGame, playerName)

        assertNull(viewModel.observeEvents().value)

        confirmVerified(mockGuestFacade)
    }

    @Test
    fun nextForHost_success() {
        every { mockHostFacade.hostGame(any(), any(), any()) } returns Completable.complete()
        val gameName = "It is ON !"

        viewModel.nextForHost(gameName, playerName)

        assertEquals(NewGameEvent.SETUP_SUCCESSFUL, viewModel.observeEvents().value)
        verify { mockHostFacade.hostGame(gameName, playerName, gamePort) }

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