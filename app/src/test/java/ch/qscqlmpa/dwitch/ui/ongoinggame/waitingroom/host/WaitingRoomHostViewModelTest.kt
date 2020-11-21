package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.host

import ch.qscqlmpa.dwitch.BaseViewModelUnitTest
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ui.common.Resource
import ch.qscqlmpa.dwitch.ui.model.UiControlModel
import ch.qscqlmpa.dwitch.ui.model.UiInfoModel
import ch.qscqlmpa.dwitch.ui.model.Visibility
import ch.qscqlmpa.dwitchcommonutil.scheduler.TestSchedulerFactory
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicationState
import ch.qscqlmpa.dwitchgame.ongoinggame.usecases.GameLaunchableEvent
import ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom.WaitingRoomHostFacade
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.PublishSubject
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class WaitingRoomHostViewModelTest : BaseViewModelUnitTest() {

    private val mockFacade = mockk<WaitingRoomHostFacade>(relaxed = true)

    private lateinit var viewModel: WaitingRoomHostViewModel

    private lateinit var communicatorSubject: PublishSubject<HostCommunicationState>

    @Before
    override fun setup() {
        super.setup()

        val schedulerFactory = TestSchedulerFactory()
        schedulerFactory.setTimeScheduler(TestScheduler())

        viewModel = WaitingRoomHostViewModel(mockFacade, ch.qscqlmpa.dwitchcommonutil.DisposableManager(), schedulerFactory)

        communicatorSubject = PublishSubject.create()
        every { mockFacade.observeCommunicationState() } returns communicatorSubject
    }

    @Test
    fun `Connection state info is updated whenever the connection state changes`() {
        val connectionStateInfo = viewModel.connectionStateInfo()
        subscribeToPublishers(connectionStateInfo)

        communicatorSubject.onNext(HostCommunicationState.Open)

        assertThat(connectionStateInfo.value!!).isEqualTo(UiInfoModel(Resource(R.string.listening_for_guests)))

        communicatorSubject.onNext(HostCommunicationState.Closed)

        assertThat(connectionStateInfo.value!!).isEqualTo(UiInfoModel(Resource(R.string.not_listening_for_guests)))

        communicatorSubject.onNext(HostCommunicationState.Error)

        assertThat(connectionStateInfo.value!!).isEqualTo(UiInfoModel(Resource(R.string.host_connection_error)))
    }

    @Test
    fun `Launch game control is enabled because game is ready to be launched`() {
        setupGameCanBeLaunchedMock(GameLaunchableEvent.GameIsReadyToBeLaunched)

        val canGameBeLaunched = viewModel.canGameBeLaunched()
        subscribeToPublishers(canGameBeLaunched)

        assertThat(canGameBeLaunched.value).isEqualTo(UiControlModel(enabled = true, visibility = Visibility.Visible))
        verify { mockFacade.gameCanBeLaunched() }
        confirmVerified(mockFacade)
    }

    @Test
    fun `Launch game control is disabled because not all players are ready`() {
        setupGameCanBeLaunchedMock(GameLaunchableEvent.NotAllPlayersAreReady)

        val canGameBeLaunched = viewModel.canGameBeLaunched()
        subscribeToPublishers(canGameBeLaunched)

        assertThat(canGameBeLaunched.value).isEqualTo(UiControlModel(enabled = false, visibility = Visibility.Visible))
        verify { mockFacade.gameCanBeLaunched() }
        confirmVerified(mockFacade)
    }

    @Test
    fun `Launch game control is disabled because there is only one player in the room`() {
        setupGameCanBeLaunchedMock(GameLaunchableEvent.NotEnoughPlayers)

        val canGameBeLaunched = viewModel.canGameBeLaunched()
        subscribeToPublishers(canGameBeLaunched)

        assertThat(canGameBeLaunched.value).isEqualTo(UiControlModel(enabled = false, visibility = Visibility.Visible))
        verify { mockFacade.gameCanBeLaunched() }
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
        every { mockFacade.gameCanBeLaunched() } returns Observable.just(event)
    }
}