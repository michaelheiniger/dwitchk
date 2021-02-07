package ch.qscqlmpa.dwitch.ui.home.hostjoinnewgame

import ch.qscqlmpa.dwitch.BaseViewModelUnitTest
import ch.qscqlmpa.dwitch.ui.home.hostnewgame.HostNewGameCommand
import ch.qscqlmpa.dwitch.ui.home.hostnewgame.HostNewGameViewModel
import ch.qscqlmpa.dwitch.ui.model.UiControlModel
import ch.qscqlmpa.dwitchgame.home.HomeHostFacade
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

class HostNewGameViewModelTest : BaseViewModelUnitTest() {

    private val mockHostFacade = mockk<HomeHostFacade>(relaxed = true)

    private lateinit var viewModel: HostNewGameViewModel

    private val gamePort = 8889

    @Before
    override fun setup() {
        super.setup()
        viewModel = HostNewGameViewModel(mockHostFacade, Schedulers.trampoline())
        every { mockHostFacade.hostGame(any(), any(), any()) } returns Completable.complete()
    }

    @Test
    fun nextControlIsInitiallyDisabled() {
        assertThat(viewModel.observeHostGameControleState().value).isEqualTo(UiControlModel(enabled = false))
    }

    @Test
    fun `Host game control is enabled when player name and game name are not blank`() {
        viewModel.onPlayerNameChange("Arthur")
        viewModel.onGameNameChange("Table Ronde")
        assertThat(viewModel.observeHostGameControleState().value).isEqualTo(UiControlModel(enabled = true))

        viewModel.onPlayerNameChange("Arthur")
        viewModel.onGameNameChange("")
        assertThat(viewModel.observeHostGameControleState().value).isEqualTo(UiControlModel(enabled = false))

        viewModel.onPlayerNameChange("Arthur")
        viewModel.onGameNameChange("Table Ronde")
        assertThat(viewModel.observeHostGameControleState().value).isEqualTo(UiControlModel(enabled = true))

        viewModel.onPlayerNameChange("")
        viewModel.onGameNameChange("Table Ronde")
        assertThat(viewModel.observeHostGameControleState().value).isEqualTo(UiControlModel(enabled = false))
    }

    @Test
    fun `Host game succesfully`() {
        val playerName = "Arthur"
        val gameName = "Table Ronde"

        viewModel.onPlayerNameChange(playerName)
        viewModel.onGameNameChange(gameName)
        viewModel.hostGame()

        assertThat(viewModel.observeCommands().value).isEqualTo(HostNewGameCommand.NavigateToWaitingRoom)
        verify { mockHostFacade.hostGame(gameName, playerName, gamePort) }
        confirmVerified(mockHostFacade)
    }

    @Test
    fun `An error is thrown if the player name is not set when hosting the game`() {
        viewModel.onPlayerNameChange("")
        viewModel.onGameNameChange("Table Ronde")

        try {
            viewModel.hostGame()
            fail("Must throw error when the player name is not set")
        } catch (e: IllegalArgumentException) {
            // Nothing to do
        }

        assertNull(viewModel.observeCommands().value)
        confirmVerified(mockHostFacade)
    }

    @Test
    fun `An error is thrown if the game name is not set when hosting the game`() {
        viewModel.onPlayerNameChange("Arthur")
        viewModel.onGameNameChange("")

        try {
            viewModel.hostGame()
            fail("Must throw error when the game name is not set")
        } catch (e: IllegalArgumentException) {
            // Nothing to do    
        }

        assertNull(viewModel.observeCommands().value)
        confirmVerified(mockHostFacade)
    }
}
