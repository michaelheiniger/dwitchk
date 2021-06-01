package ch.qscqlmpa.dwitch.ui.home.hostjoinnewgame

import ch.qscqlmpa.dwitch.BuildConfig
import ch.qscqlmpa.dwitch.app.AppEvent
import ch.qscqlmpa.dwitch.app.AppEventRepository
import ch.qscqlmpa.dwitch.ui.BaseViewModelUnitTest
import ch.qscqlmpa.dwitch.ui.home.hostnewgame.HostNewGameNavigationCommand
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
import org.junit.Assert.assertNull
import org.junit.Assume
import org.junit.Before
import org.junit.Test

class HostNewGameViewModelTest : BaseViewModelUnitTest() {

    private val mockAppEventRepository = mockk<AppEventRepository>(relaxed = true)
    private val mockHostFacade = mockk<HomeHostFacade>(relaxed = true)

    private lateinit var viewModel: HostNewGameViewModel

    private val gamePort = 8889

    @Before
    fun setup() {
        viewModel = HostNewGameViewModel(mockAppEventRepository, mockHostFacade, Schedulers.trampoline())
        every { mockHostFacade.hostGame(any(), any(), any()) } returns Completable.complete()
    }

    @Test
    fun `Create game control is initially disabled`() {
        Assume.assumeFalse("We are in debug variant", BuildConfig.DEBUG)

        // Given initial state, then join game control is disabled
        assertThat(viewModel.createGameControl.value).isFalse
    }

    @Test
    fun `Create game control is enabled when player name and game name are not blank`() {
        viewModel.onPlayerNameChange("Arthur")
        viewModel.onGameNameChange("Table Ronde")
        assertThat(viewModel.createGameControl.value).isTrue

        viewModel.onPlayerNameChange("Arthur")
        viewModel.onGameNameChange("")
        assertThat(viewModel.createGameControl.value).isFalse

        viewModel.onPlayerNameChange("Arthur")
        viewModel.onGameNameChange("Table Ronde")
        assertThat(viewModel.createGameControl.value).isTrue

        viewModel.onPlayerNameChange("")
        viewModel.onGameNameChange("Table Ronde")
        assertThat(viewModel.createGameControl.value).isFalse
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
        assertThat(viewModel.navigationCommand.value).isEqualTo(HostNewGameNavigationCommand.NavigateToWaitingRoom)
        verify { mockHostFacade.hostGame(gameName, playerName, gamePort) }
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
        assertNull(viewModel.navigationCommand.value)
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
        assertNull(viewModel.navigationCommand.value)
        confirmVerified(mockHostFacade)
    }
}
