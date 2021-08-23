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
import org.junit.Assume
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE)
@RunWith(RobolectricTestRunner::class)
class JoinNewGameViewModelTest : BaseViewModelUnitTest() {

    private val mockAppEventRepository = mockk<AppEventRepository>(relaxed = true)
    private val mockGuestFacade = mockk<HomeGuestFacade>(relaxed = true)

    private lateinit var viewModel: JoinNewGameViewModel

    private val gameIpAddress = "192.168.1.1"
    private val advertisedGame = AdvertisedGame(true, "Table Ronde", GameCommonId(1), gameIpAddress, 8889)

    @Before
    fun setup() {
        viewModel = JoinNewGameViewModel(mockAppEventRepository, mockGuestFacade, Schedulers.trampoline())
        every { mockGuestFacade.joinGame(any(), any()) } returns Completable.complete()
        every { mockGuestFacade.getAdvertisedGame(gameIpAddress) } returns advertisedGame
    }

    @Test
    fun `Join game control is initially disabled`() {
        Assume.assumeFalse("We are in debug variant", BuildConfig.DEBUG)

        // Given initial state, then join game control is disabled
        assertThat(viewModel.joinGameControlEnabled.value).isEqualTo(false)
    }

    @Test
    fun `Join game control is enabled when player name is not blank`() {
        viewModel.onPlayerNameChange("Arthur")
        assertThat(viewModel.joinGameControlEnabled.value).isEqualTo(true)

        viewModel.onPlayerNameChange("")
        assertThat(viewModel.joinGameControlEnabled.value).isEqualTo(false)

        viewModel.onPlayerNameChange("Arthur")
        assertThat(viewModel.joinGameControlEnabled.value).isEqualTo(true)
    }

    @Test
    fun `Navigate to the WaitingRoom when game is successfully created`() {
        // Given
        val playerName = "Arthur"
        every { mockAppEventRepository.observeEvents() } returns Observable.just(AppEvent.ServiceStarted)
        every { mockGuestFacade.joinGame(any(), any()) } returns Completable.complete()

        // When
        viewModel.onPlayerNameChange(playerName)
        viewModel.joinGame(gameIpAddress)

        // Then
        assertThat(viewModel.navigation.value).isEqualTo(JoinNewGameDestination.NavigateToWaitingRoom)
        verify { mockGuestFacade.getAdvertisedGame(any()) }
        verify { mockGuestFacade.joinGame(advertisedGame, playerName) }
        confirmVerified(mockGuestFacade)
    }

    @Test
    fun `Display notification when game cannot be found (eg advertising has stopped) `() {
        // Given
        val playerName = "Arthur"
        every { mockAppEventRepository.observeEvents() } returns Observable.just(AppEvent.ServiceStarted)
        every { mockGuestFacade.getAdvertisedGame(gameIpAddress) } returns null

        // When
        viewModel.onPlayerNameChange(playerName)
        viewModel.joinGame(gameIpAddress)

        // Then
        assertThat(viewModel.notification.value).isEqualTo(JoinNewGameNotification.GameNotFound)
        verify { mockGuestFacade.getAdvertisedGame(any()) }
        confirmVerified(mockGuestFacade)
    }

    @Test
    fun `Navigate to the HomeScreen when game not found notification is acknowledged`() {
        // Given
        val playerName = "Arthur"
        every { mockAppEventRepository.observeEvents() } returns Observable.just(AppEvent.ServiceStarted)
        every { mockGuestFacade.getAdvertisedGame(gameIpAddress) } returns null

        // When
        viewModel.onPlayerNameChange(playerName)
        viewModel.joinGame(gameIpAddress)
        viewModel.onGameNotFoundAcknowledge()

        // Then
        assertThat(viewModel.navigation.value).isEqualTo(JoinNewGameDestination.NavigateToHomeScreen)
        verify { mockGuestFacade.getAdvertisedGame(any()) }
        confirmVerified(mockGuestFacade)
    }

    @Test
    fun `An error is thrown if the player name is not set when joining the game`() {
        // Given
        viewModel.onPlayerNameChange("")

        // When the required data is not provided, the "create game" control is supposed to be disabled.
        // Hence the user should not be able to launch the game creation
        try {
            viewModel.joinGame(gameIpAddress)
            fail("Must throw error when player name is not set")
        } catch (e: IllegalArgumentException) {
            // Nothing to do
        }

        // Then
        assertThat(viewModel.navigation.value).isEqualTo(JoinNewGameDestination.CurrentScreen)
        verify { mockGuestFacade.getAdvertisedGame(any()) }
        confirmVerified(mockGuestFacade)
    }
}
