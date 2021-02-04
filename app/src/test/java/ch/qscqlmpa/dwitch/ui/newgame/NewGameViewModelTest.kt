package ch.qscqlmpa.dwitch.ui.newgame

import ch.qscqlmpa.dwitch.BaseViewModelUnitTest
import ch.qscqlmpa.dwitch.ui.home.newgame.NewGameViewModel
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
import io.reactivex.rxjava3.core.Completable
import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class NewGameViewModelTest : BaseViewModelUnitTest() {

    private val mockHostFacade = mockk<HomeHostFacade>(relaxed = true)
    private val mockGuestFacade = mockk<HomeGuestFacade>(relaxed = true)

    private lateinit var viewModel: NewGameViewModel

    private val playerName = "Bernard"
    private val gamePort = 8889

    @Before
    override fun setup() {
        super.setup()
        viewModel = NewGameViewModel(mockHostFacade, mockGuestFacade,
            ch.qscqlmpa.dwitchcommonutil.DisposableManager(), TestSchedulerFactory())
    }

    @Test
    fun nextForGuest_success() {
        every { mockGuestFacade.joinGame(any(), any()) } returns Completable.complete()

        val advertisedGame = AdvertisedGame(true, "Dwiiitch !", GameCommonId(1), "192.168.1.1", gamePort)

        viewModel.nextForGuest(advertisedGame, playerName)

        assertThat(viewModel.observeEvents().value).isEqualTo(NewGameEvent.SETUP_SUCCESSFUL)
        verify { mockGuestFacade.joinGame(advertisedGame, playerName) }

        confirmVerified(mockGuestFacade)
    }

    @Test
    fun nextForGuest_error() {
        val advertisedGame = AdvertisedGame(true, "", GameCommonId(1), "192.168.1.1", gamePort)

        viewModel.nextForGuest(advertisedGame, playerName)

        assertNull(viewModel.observeEvents().value)

        confirmVerified(mockGuestFacade)
    }

    @Test
    fun nextForHost_success() {
        every { mockHostFacade.hostGame(any(), any(), any()) } returns Completable.complete()
        val gameName = "It is ON !"

        viewModel.nextForHost(gameName, playerName)

        assertThat(viewModel.observeEvents().value).isEqualTo(NewGameEvent.SETUP_SUCCESSFUL)
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