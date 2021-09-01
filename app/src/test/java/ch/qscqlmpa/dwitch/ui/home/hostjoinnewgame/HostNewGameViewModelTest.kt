package ch.qscqlmpa.dwitch.ui.home.hostjoinnewgame

import ch.qscqlmpa.dwitch.BuildConfig
import ch.qscqlmpa.dwitch.app.AppEvent
import ch.qscqlmpa.dwitch.app.AppEventRepository
import ch.qscqlmpa.dwitch.ui.BaseViewModelUnitTest
import ch.qscqlmpa.dwitch.ui.Destination
import ch.qscqlmpa.dwitch.ui.NavigationBridge
import ch.qscqlmpa.dwitch.ui.home.hostnewgame.HostNewGameViewModel
import ch.qscqlmpa.dwitchgame.home.HomeHostFacade
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

class HostNewGameViewModelTest : BaseViewModelUnitTest() {

    private val mockAppEventRepository = mockk<AppEventRepository>(relaxed = true)
    private val mockHostFacade = mockk<HomeHostFacade>(relaxed = true)
    private val mockNavigationBridge = mockk<NavigationBridge>(relaxed = true)

    private lateinit var viewModel: HostNewGameViewModel

    @Before
    fun setup() {
        viewModel = HostNewGameViewModel(
            mockAppEventRepository,
            mockHostFacade,
            mockk(relaxed = true),
            mockNavigationBridge,
            Schedulers.trampoline()
        )
        every { mockHostFacade.hostGame(any(), any()) } returns Completable.complete()
    }

    @Test
    fun `should initially prevent creating the game`() {
        Assume.assumeFalse("We are in debug variant", BuildConfig.DEBUG)

        // Given initial state, then the game may not be created
        assertThat(viewModel.canGameBeCreated.value).isFalse
    }

    @Test
    fun `Create game control is enabled when player name and game name are not blank`() {
        // Given initial state

        // When a player and game name are set
        viewModel.onPlayerNameChange("Arthur")
        viewModel.onGameNameChange("Table Ronde")

        // Then the game can be created
        assertThat(viewModel.canGameBeCreated.value).isTrue

        // When the game name is blank
        viewModel.onGameNameChange("")

        // Then the game cannot be created
        assertThat(viewModel.canGameBeCreated.value).isFalse

        viewModel.onPlayerNameChange("Arthur")
        viewModel.onGameNameChange("Table Ronde")
        assertThat(viewModel.canGameBeCreated.value).isTrue

        // When the player name is blank
        viewModel.onPlayerNameChange("")

        // Then the game cannot be created
        assertThat(viewModel.canGameBeCreated.value).isFalse
    }

    @Test
    fun `Navigate to the WaitingRoom when game is successfully created`() {
        // Given
        val playerName = "Arthur"
        val gameName = "Table Ronde"
        every { mockAppEventRepository.observeEvents() } returns Observable.just(AppEvent.ServiceStarted)

        // When
        viewModel.onPlayerNameChange(playerName)
        viewModel.onGameNameChange(gameName)
        viewModel.hostGame()

        // Then
        verify { mockNavigationBridge.navigate(Destination.HomeScreens.InGame) }
        verify { mockHostFacade.hostGame(gameName, playerName) }
        confirmVerified(mockHostFacade)
    }

    @Test
    fun `An error is thrown if the player name is not set when hosting the game`() {
        // Given
        viewModel.onPlayerNameChange("")
        viewModel.onGameNameChange("Table Ronde")

        // When the required data is not provided, the "create game" control is supposed to be disabled.
        // Hence the user should not be able to launch the game creation
        try {
            viewModel.hostGame()
            fail("Must throw error when the player name is not set")
        } catch (e: IllegalArgumentException) {
            // Nothing to do
        }

        // Then
        verify(exactly = 0) { mockNavigationBridge.navigate(any()) }
        confirmVerified(mockHostFacade)
    }

    @Test
    fun `An error is thrown if the game name is not set when hosting the game`() {
        // Given
        viewModel.onPlayerNameChange("Arthur")
        viewModel.onGameNameChange("")

        // When the required data is not provided, the "create game" control is supposed to be disabled.
        // Hence the user should not be able to launch the game creation
        try {
            viewModel.hostGame()
            fail("Must throw error when the game name is not set")
        } catch (e: IllegalArgumentException) {
            // Nothing to do    
        }

        // Then
        verify(exactly = 0) { mockNavigationBridge.navigate(any()) }
        confirmVerified(mockHostFacade)
    }
}
