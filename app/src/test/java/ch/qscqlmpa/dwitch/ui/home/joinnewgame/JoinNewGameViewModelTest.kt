package ch.qscqlmpa.dwitch.ui.home.joinnewgame

import ch.qscqlmpa.dwitch.BuildConfig
import ch.qscqlmpa.dwitch.app.StubIdlingResource
import ch.qscqlmpa.dwitch.ui.BaseViewModelUnitTest
import ch.qscqlmpa.dwitch.ui.Destination
import ch.qscqlmpa.dwitch.ui.NavigationBridge
import ch.qscqlmpa.dwitchcommunication.GameAdvertisingInfo
import ch.qscqlmpa.dwitchgame.game.GameFacade
import ch.qscqlmpa.dwitchgame.gamediscovery.GameDiscoveryFacade
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

// TODO: Add tests for cases where the loaded game is froma QR code.
class JoinNewGameViewModelTest : BaseViewModelUnitTest() {

    private val mockGameFacade = mockk<GameFacade>(relaxed = true)
    private val mockGameDiscoveryFacade = mockk<GameDiscoveryFacade>(relaxed = true)
    private val mockNavigationBridge = mockk<NavigationBridge>(relaxed = true)

    private lateinit var viewModel: JoinNewGameViewModel

    private val gameCommonId = GameCommonId(UUID.randomUUID())
    private val advertisedGame = GameAdvertisingInfo(true, "Table Ronde", gameCommonId, "192.168.1.1", 8889)
    private val qrCodeContent =
        "{\"isNew\": true, \"gameCommonId\":\"${gameCommonId.value}\",\"gameName\":\"Table Ronde\", \"gameIpAddress\":\"192.168.1.1\", \"gamePort\":8889}"

    @Before
    fun setup() {
        viewModel = JoinNewGameViewModel(
            mockGameFacade,
            mockGameDiscoveryFacade,
            mockNavigationBridge,
            Schedulers.trampoline(),
            StubIdlingResource()
        )
        every { mockGameFacade.joinGame(any(), any()) } returns Completable.complete()
        every { mockGameDiscoveryFacade.getAdvertisedGame(gameCommonId) } returns advertisedGame
        every { mockGameDiscoveryFacade.getAdvertisedGame(qrCodeContent) } returns advertisedGame
    }

    private fun gameCannotBeJoinedInitially(loadGame: () -> Unit) {
        Assume.assumeFalse("We are in debug variant", BuildConfig.DEBUG)
        // TODO: call viewModel.start() ? --> do in all VM tests
        loadGame()

        // Given initial state, then the game cannot be joined
        assertThat(viewModel.canJoinGame.value).isEqualTo(false)
    }

    @Test
    fun `The game cannot be joined initially - advertised game`() {
        gameCannotBeJoinedInitially { viewModel.loadGame(gameCommonId) }
    }

    @Test
    fun `The game cannot be joined initially - QR code`() {
        gameCannotBeJoinedInitially { viewModel.loadGame(qrCodeContent) }
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
        viewModel.loadGame(gameCommonId)
        viewModel.onPlayerNameChange(playerName)
        every { mockGameFacade.joinGame(any(), any()) } returns Completable.complete()

        // When
        viewModel.joinGame()

        // Then
        verify { mockNavigationBridge.navigate(Destination.HomeScreens.InGame) }
        verify { mockGameFacade.joinGame(advertisedGame, playerName) }
    }

    @Test
    fun `Display notification when game cannot be found (eg advertising has stopped) `() {
        // Given
        viewModel.onPlayerNameChange("Arthur")
        every { mockGameDiscoveryFacade.getAdvertisedGame(gameCommonId) } returns null

        // When
        viewModel.loadGame(gameCommonId)

        // Then
        assertThat(viewModel.notification.value).isEqualTo(JoinNewGameNotification.GameNotFound)
        verify { mockGameDiscoveryFacade.getAdvertisedGame(any<GameCommonId>()) }
    }

    @Test
    fun `Navigate to the HomeScreen when game not found notification is acknowledged`() {
        // Given
        val playerName = "Arthur"
        viewModel.loadGame(gameCommonId)
        viewModel.onPlayerNameChange(playerName)
        every { mockGameDiscoveryFacade.getAdvertisedGame(gameCommonId) } returns null
        viewModel.joinGame()

        // When
        viewModel.onGameNotFoundAcknowledge()

        // Then
        verify { mockNavigationBridge.navigate(Destination.HomeScreens.Home) }
        verify { mockGameDiscoveryFacade.getAdvertisedGame(any<GameCommonId>()) }
    }

    @Suppress("SwallowedException")
    @Test
    fun `An error is thrown if the player name is not set when joining the game`() {
        // Given
        viewModel.loadGame(gameCommonId)
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
        verify { mockGameDiscoveryFacade.getAdvertisedGame(any<GameCommonId>()) }
    }
}
