package ch.qscqlmpa.dwitch.ui.home.joinnewgame

import ch.qscqlmpa.dwitch.ui.BaseViewModelUnitTest
import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitchgame.home.HomeGuestFacade
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.fail
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test

class JoinNewGameViewModelTest : BaseViewModelUnitTest() {

    private val mockGuestFacade = mockk<HomeGuestFacade>(relaxed = true)

    private lateinit var viewModel: JoinNewGameViewModel

    private val advertisedGame = AdvertisedGame(true, "Table Ronde", GameCommonId(1), "192.168.1.1", 8889)

    @Before
    fun setup() {
        viewModel = JoinNewGameViewModel(mockGuestFacade, Schedulers.trampoline())
        every { mockGuestFacade.joinGame(any(), any()) } returns Completable.complete()
    }

    @Test
    fun `Join game control is initially disabled`() {
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
        val playerName = "Arthur"

        viewModel.onPlayerNameChange(playerName)
        viewModel.joinGame(advertisedGame)

        assertThat(viewModel.commands.value).isEqualTo(JoinNewGameCommand.NavigateToWaitingRoom)
        verify { mockGuestFacade.joinGame(advertisedGame, playerName) }
        confirmVerified(mockGuestFacade)
    }

    @Test
    fun `An error is thrown if the player name is not set when joining the game`() {
        viewModel.onPlayerNameChange("")

        // The "create game" control is supposed to be disabled as long as required data is not provided
        // Hence the user should not be able to launch the game creation
        try {
            viewModel.joinGame(advertisedGame)
            fail("Must throw error when player name is not set")
        } catch (e: IllegalArgumentException) {
            // Nothing to do
        }

        assertNull(viewModel.commands.value)
        confirmVerified(mockGuestFacade)
    }
}
