package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.host

import ch.qscqlmpa.dwitch.ui.BaseViewModelUnitTest
import ch.qscqlmpa.dwitch.ui.model.UiControlModel
import ch.qscqlmpa.dwitch.ui.model.Visibility
import ch.qscqlmpa.dwitchcommonutil.scheduler.TestSchedulerFactory
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicationState
import ch.qscqlmpa.dwitchgame.ongoinggame.usecases.GameLaunchableEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom.WaitingRoomHostFacade
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.schedulers.TestScheduler
import io.reactivex.rxjava3.subjects.PublishSubject
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class) // Needed because of logging
class WaitingRoomHostViewModelTest : BaseViewModelUnitTest() {

    private val mockFacade = mockk<WaitingRoomHostFacade>(relaxed = true)

    private lateinit var viewModel: WaitingRoomHostViewModel

    private lateinit var communicatorSubject: PublishSubject<HostCommunicationState>

    @Before
    override fun setup() {
        super.setup()

        val schedulerFactory = TestSchedulerFactory()
        schedulerFactory.setTimeScheduler(TestScheduler())

        viewModel = WaitingRoomHostViewModel(mockFacade, Schedulers.trampoline())

        communicatorSubject = PublishSubject.create()
        every { mockFacade.observeCommunicationState() } returns communicatorSubject
    }

    @Test
    fun `Launch game control is enabled because game is ready to be launched`() {
        setupGameCanBeLaunchedMock(GameLaunchableEvent.GameIsReadyToBeLaunched)

        val canGameBeLaunched = viewModel.canGameBeLaunched()
        subscribeToPublishers(canGameBeLaunched)

        assertThat(canGameBeLaunched.value).isEqualTo(UiControlModel(enabled = true, visibility = Visibility.Visible))
        verify { mockFacade.observeGameLaunchableEvents() }
        confirmVerified(mockFacade)
    }

    @Test
    fun `Launch game control is disabled because not all players are ready`() {
        setupGameCanBeLaunchedMock(GameLaunchableEvent.NotAllPlayersAreReady)

        val canGameBeLaunched = viewModel.canGameBeLaunched()
        subscribeToPublishers(canGameBeLaunched)

        assertThat(canGameBeLaunched.value).isEqualTo(UiControlModel(enabled = false, visibility = Visibility.Visible))
        verify { mockFacade.observeGameLaunchableEvents() }
        confirmVerified(mockFacade)
    }

    @Test
    fun `Launch game control is disabled because there is only one player in the room`() {
        setupGameCanBeLaunchedMock(GameLaunchableEvent.NotEnoughPlayers)

        val canGameBeLaunched = viewModel.canGameBeLaunched()
        subscribeToPublishers(canGameBeLaunched)

        assertThat(canGameBeLaunched.value).isEqualTo(UiControlModel(enabled = false, visibility = Visibility.Visible))
        verify { mockFacade.observeGameLaunchableEvents() }
        confirmVerified(mockFacade)
    }

    @Test
    fun `Launch game`() {
        every { mockFacade.launchGame() } returns Completable.complete()

        viewModel.launchGame()

        verify { mockFacade.launchGame() }
        confirmVerified(mockFacade)
    }

    @Test
    fun `Publish command to navigate to game room when game is launched`() {
        every { mockFacade.launchGame() } returns Completable.complete()

        val commands = viewModel.commands()
        subscribeToPublishers(commands)

        viewModel.launchGame()

        assertThat(commands.value).isEqualTo(WaitingRoomHostCommand.NavigateToGameRoomScreen)
    }

    @Test
    fun `Cancel game`() {
        every { mockFacade.cancelGame() } returns Completable.complete()

        viewModel.cancelGame()

        verify { mockFacade.cancelGame() }
        confirmVerified(mockFacade)
    }

    @Test
    fun `Publish command to navigate to home screen when game is canceled`() {
        every { mockFacade.cancelGame() } returns Completable.complete()

        val commands = viewModel.commands()
        subscribeToPublishers(commands)

        viewModel.cancelGame()

        assertThat(commands.value).isEqualTo(WaitingRoomHostCommand.NavigateToHomeScreen)
    }

    private fun setupGameCanBeLaunchedMock(event: GameLaunchableEvent) {
        every { mockFacade.observeGameLaunchableEvents() } returns Observable.just(event)
    }
}
