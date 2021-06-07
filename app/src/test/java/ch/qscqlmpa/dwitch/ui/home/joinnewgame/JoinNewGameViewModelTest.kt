package ch.qscqlmpa.dwitch.ui.home.joinnewgame

import ch.qscqlmpa.dwitch.BuildConfig
import ch.qscqlmpa.dwitch.app.AppEvent
import ch.qscqlmpa.dwitch.app.AppEventRepository
import ch.qscqlmpa.dwitch.ui.BaseViewModelUnitTest
import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitchgame.home.HomeGuestFacade
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.Assert.assertNull
import org.junit.Assume
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class JoinNewGameViewModelTest : BaseViewModelUnitTest() {

    private val mockAppEventRepository = mockk<AppEventRepository>(relaxed = true)
    private val mockGuestFacade = mockk<HomeGuestFacade>(relaxed = true)

    private lateinit var viewModel: JoinNewGameViewModel

    private val advertisedGame = AdvertisedGame(true, "Table Ronde", GameCommonId(1), "192.168.1.1", 8889)

    @Before
    fun setup() {
        viewModel = JoinNewGameViewModel(mockAppEventRepository, mockGuestFacade, Schedulers.trampoline())
        every { mockGuestFacade.joinGame(any(), any()) } returns Completable.complete()
        every { mockGuestFacade.getAdvertisedGame("192.168.1.1") } returns advertisedGame
    }

    @Test
    fun `Join game control is initially disabled`() {
        Assume.assumeFalse("We are in debug variant", BuildConfig.DEBUG)

        // Given initial state, then join game control is disabled
        assertThat(viewModel.joinGameControl.value).isEqualTo(false)
    }

    @Test
    fun `Join game control is enabled when player name is not blank`() {
        viewModel.onPlayerNameChange("Arthur")
        assertThat(viewModel.joinGameControl.value).isEqualTo(true)

        viewModel.onPlayerNameChange("")
        assertThat(viewModel.joinGameControl.value).isEqualTo(false)

        viewModel.onPlayerNameChange("Arthur")
        assertThat(viewModel.joinGameControl.value).isEqualTo(true)
    }

    @Test
    fun `Navigate to the WaitingRoom when game is successfully created`() {
        // Given
        val playerName = "Arthur"
        every { mockAppEventRepository.observeEvents() } returns Observable.just(AppEvent.ServiceStarted)

        // When
        viewModel.onPlayerNameChange(playerName)
        viewModel.getGame("192.168.1.1")
        viewModel.joinGame()

        // Then
        assertThat(viewModel.navigationCommand.value).isEqualTo(JoinNewGameDestination.NavigateToWaitingRoom)
        verify { mockGuestFacade.getAdvertisedGame(any()) }
        verify { mockGuestFacade.joinGame(advertisedGame, playerName) }
        confirmVerified(mockGuestFacade)
    }

    @Test
    fun `An error is thrown if the player name is not set when joining the game`() {
        // Given
        viewModel.onPlayerNameChange("")

        // When the required data is not provided, the "create game" control is supposed to be disabled.
        // Hence the user should not be able to launch the game creation
        try {
            viewModel.joinGame()
            fail("Must throw error when player name is not set")
        } catch (e: IllegalArgumentException) {
            // Nothing to do
        }

        // Then
        assertNull(viewModel.navigationCommand.value)
        confirmVerified(mockGuestFacade)
    }
}
