package ch.qscqlmpa.dwitch.ui.home.joinnewgame

import ch.qscqlmpa.dwitch.BuildConfig
import ch.qscqlmpa.dwitch.app.StubIdlingResource
import ch.qscqlmpa.dwitch.ui.BaseViewModelUnitTest
import ch.qscqlmpa.dwitch.ui.navigation.HomeScreens
import ch.qscqlmpa.dwitch.ui.navigation.NavigationBridge
import ch.qscqlmpa.dwitchcommunication.GameAdvertisingInfo
import ch.qscqlmpa.dwitchgame.game.GameFacade
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.Assume
import org.junit.Before
import org.junit.Test
import java.util.*

class JoinNewGameViewModelTest : BaseViewModelUnitTest() {

    private val mockGameFacade = mockk<GameFacade>(relaxed = true)
    private val mockNavigationBridge = mockk<NavigationBridge>(relaxed = true)

    private lateinit var viewModel: JoinNewGameViewModel

    private val gameCommonId = GameCommonId(UUID.randomUUID())
    private val advertisedGame = GameAdvertisingInfo(true, "Table Ronde", gameCommonId, "192.168.1.1", 8889)

    @Before
    fun setup() {
        viewModel = JoinNewGameViewModel(
            mockGameFacade,
            mockNavigationBridge,
            Schedulers.trampoline(),
            StubIdlingResource()
        )
        every { mockGameFacade.joinGame(any(), any()) } returns Completable.complete()
    }

    @Test
    fun `The game cannot be joined initially -`() {
        Assume.assumeFalse("We are in debug variant", BuildConfig.DEBUG)
        // TODO: call viewModel.start() ? --> do in all VM tests
        viewModel.loadGame(advertisedGame)

        // Given initial state, then the game cannot be joined
        assertThat(viewModel.canJoinGame.value).isEqualTo(false)
    }

    @Test
    fun `The game can be joined when player name is not blank`() {
        // Given initial state

        // When a player name is set
        viewModel.onPlayerNameChange("Arthur")

        // Then the game can be joined
        assertThat(viewModel.canJoinGame.value).isEqualTo(true)

        // When the player name is erased
        viewModel.onPlayerNameChange("")

        // Then the game cannot be joined
        assertThat(viewModel.canJoinGame.value).isEqualTo(false)

        // When the player name is set
        viewModel.onPlayerNameChange("Arthur")

        // Then the game can be joined
        assertThat(viewModel.canJoinGame.value).isEqualTo(true)
    }

    @Test
    fun `Navigate to InGame when game is successfully joined`() {
        // Given
        val playerName = "Arthur"
        viewModel.loadGame(advertisedGame)
        viewModel.onPlayerNameChange(playerName)
        every { mockGameFacade.joinGame(any(), any()) } returns Completable.complete()

        // When
        viewModel.joinGame()

        // Then
        verify { mockNavigationBridge.navigate(HomeScreens.InGame) }
        verify { mockGameFacade.joinGame(advertisedGame, playerName) }
    }

    @Suppress("SwallowedException")
    @Test
    fun `An error is thrown if the player name is not set when joining the game`() {
        // Given
        viewModel.loadGame(advertisedGame)
        viewModel.onPlayerNameChange("")
        every { mockGameFacade.joinGame(any(), any()) } returns Completable.complete()

        // When the required data is not provided, the "create game" control is supposed to be disabled.
        // Hence the user should not be able to launch the game creation
        try {
            viewModel.joinGame()
            fail("Must throw error when player name is not set")
        } catch (e: IllegalArgumentException) {
            // Nothing to do
        }

        // Then
        verify(exactly = 0) { mockNavigationBridge.navigate(any()) }
    }
}
