package ch.qscqlmpa.dwitch.ui.home.joinnewgame

import ch.qscqlmpa.dwitch.BaseViewModelUnitTest
import ch.qscqlmpa.dwitch.ui.model.UiControlModel
import ch.qscqlmpa.dwitchcommonutil.DisposableManager
import ch.qscqlmpa.dwitchcommonutil.scheduler.TestSchedulerFactory
import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitchgame.home.HomeGuestFacade
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Completable
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
    override fun setup() {
        super.setup()
        viewModel = JoinNewGameViewModel(mockGuestFacade, DisposableManager(), TestSchedulerFactory())
        every { mockGuestFacade.joinGame(any(), any()) } returns Completable.complete()
    }

    @Test
    fun `Join game control is initially disabled`() {
        assertThat(viewModel.observeJoinGameControlState().value).isEqualTo(UiControlModel(enabled = false))
    }

    @Test
    fun `Join game control is enabled when player name is not blank`() {
        viewModel.onPlayerNameChange("Arthur")
        assertThat(viewModel.observeJoinGameControlState().value).isEqualTo(UiControlModel(enabled = true))

        viewModel.onPlayerNameChange("")
        assertThat(viewModel.observeJoinGameControlState().value).isEqualTo(UiControlModel(enabled = false))

        viewModel.onPlayerNameChange("Arthur")
        assertThat(viewModel.observeJoinGameControlState().value).isEqualTo(UiControlModel(enabled = true))
    }

    @Test
    fun `Join game successfully`() {
        val playerName = "Arthur"

        viewModel.onPlayerNameChange(playerName)
        viewModel.joinGame(advertisedGame)

        assertThat(viewModel.observeCommands().value).isEqualTo(JoinNewGameCommand.NavigateToWaitingRoom)
        verify { mockGuestFacade.joinGame(advertisedGame, playerName) }

        confirmVerified(mockGuestFacade)
    }

    @Test
    fun `An error is thrown if the player name is not set when joining the game`() {
        viewModel.onPlayerNameChange("")
        try {
            viewModel.joinGame(advertisedGame)
            fail("Must throw error when player name is not set")
        } catch (e: IllegalArgumentException) {
            // Nothing to do
        }

        assertNull(viewModel.observeCommands().value)
        confirmVerified(mockGuestFacade)
    }
}
