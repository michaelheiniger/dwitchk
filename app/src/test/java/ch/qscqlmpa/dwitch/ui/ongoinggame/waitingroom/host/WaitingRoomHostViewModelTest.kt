package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.host

import ch.qscqlmpa.dwitch.BaseViewModelUnitTest
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.HostCommunicationState
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.usecases.CancelGameUsecase
import ch.qscqlmpa.dwitch.ongoinggame.usecases.GameLaunchableEvent
import ch.qscqlmpa.dwitch.ongoinggame.usecases.GameLaunchableUsecase
import ch.qscqlmpa.dwitch.ongoinggame.usecases.LaunchGameUsecase
import ch.qscqlmpa.dwitch.scheduler.TestSchedulerFactory
import ch.qscqlmpa.dwitch.ui.model.UiControlModel
import ch.qscqlmpa.dwitch.ui.model.UiInfoModel
import ch.qscqlmpa.dwitch.ui.model.Visibility
import ch.qscqlmpa.dwitch.utils.DisposableManager
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

    private val mockCommunicator = mockk<HostCommunicator>(relaxed = true)

    private val mockGameLaunchableUsecase = mockk<GameLaunchableUsecase>(relaxed = true)

    private val mockCancelGameUsecase = mockk<CancelGameUsecase>(relaxed = true)

    private val mockLaunchGameUsecase = mockk<LaunchGameUsecase>(relaxed = true)

    private lateinit var viewModel: WaitingRoomHostViewModel

    private lateinit var communicatorSubject: PublishSubject<HostCommunicationState>

    @Before
    override fun setup() {
        super.setup()

        val schedulerFactory = TestSchedulerFactory()
        schedulerFactory.setTimeScheduler(TestScheduler())

        viewModel = WaitingRoomHostViewModel(
            mockCommunicator,
            mockGameLaunchableUsecase,
            mockLaunchGameUsecase,
            mockCancelGameUsecase,
            DisposableManager(),
            schedulerFactory
        )

        communicatorSubject = PublishSubject.create()
        every { mockCommunicator.observeCommunicationState() } returns communicatorSubject
    }

    @Test
    fun `Connection state info is updated whenever the connection state changes`() {
        val connectionStateInfo = viewModel.connectionStateInfo()
        subscribeToPublishers(connectionStateInfo)

        communicatorSubject.onNext(HostCommunicationState.ListeningForGuests)

        assertThat(connectionStateInfo.value!!).isEqualTo(UiInfoModel(HostCommunicationState.ListeningForGuests.resource))

        communicatorSubject.onNext(HostCommunicationState.NotListeningForGuests)

        assertThat(connectionStateInfo.value!!).isEqualTo(UiInfoModel(HostCommunicationState.NotListeningForGuests.resource))

        communicatorSubject.onNext(HostCommunicationState.Error)

        assertThat(connectionStateInfo.value!!).isEqualTo(UiInfoModel(HostCommunicationState.Error.resource))
    }

    @Test
    fun `Launch game control is enabled because game is ready to be launched`() {
        setupGameCanBeLaunchedMock(GameLaunchableEvent.GameIsReadyToBeLaunched)

        val canGameBeLaunched = viewModel.canGameBeLaunched()
        subscribeToPublishers(canGameBeLaunched)

        assertThat(canGameBeLaunched.value).isEqualTo(UiControlModel(enabled = true, visibility = Visibility.Visible))
        verify { mockGameLaunchableUsecase.gameCanBeLaunched() }
        confirmVerified(mockGameLaunchableUsecase)
    }

    @Test
    fun `Launch game control is disabled because not all players are ready`() {
        setupGameCanBeLaunchedMock(GameLaunchableEvent.NotAllPlayersAreReady)

        val canGameBeLaunched = viewModel.canGameBeLaunched()
        subscribeToPublishers(canGameBeLaunched)

        assertThat(canGameBeLaunched.value).isEqualTo(UiControlModel(enabled = false, visibility = Visibility.Visible))
        verify { mockGameLaunchableUsecase.gameCanBeLaunched() }
        confirmVerified(mockGameLaunchableUsecase)
    }

    @Test
    fun `Launch game control is disabled because there is only one player in the room`() {
        setupGameCanBeLaunchedMock(GameLaunchableEvent.NotEnoughPlayers)

        val canGameBeLaunched = viewModel.canGameBeLaunched()
        subscribeToPublishers(canGameBeLaunched)

        assertThat(canGameBeLaunched.value).isEqualTo(UiControlModel(enabled = false, visibility = Visibility.Visible))
        verify { mockGameLaunchableUsecase.gameCanBeLaunched() }
        confirmVerified(mockGameLaunchableUsecase)
    }

    @Test
    fun `Launch game`() {
        every { mockLaunchGameUsecase.launchGame() } returns Completable.complete()

        viewModel.launchGame()

        verify { mockLaunchGameUsecase.launchGame() }
        confirmVerified(mockLaunchGameUsecase)
    }

    @Test
    fun `Publish command to navigate to game room when game is launched`() {
        every { mockLaunchGameUsecase.launchGame() } returns Completable.complete()

        val commands = viewModel.commands()
        subscribeToPublishers(commands)

        viewModel.launchGame()

        assertThat(commands.value).isEqualTo(WaitingRoomHostCommand.NavigateToGameRoomScreen)
    }

    @Test
    fun `Cancel game`() {
        every { mockCancelGameUsecase.cancelGame() } returns Completable.complete()

        viewModel.cancelGame()

        verify { mockCancelGameUsecase.cancelGame() }
        confirmVerified(mockCancelGameUsecase)
    }

    @Test
    fun `Publish command to navigate to home screen when game is canceled`() {
        every { mockCancelGameUsecase.cancelGame() } returns Completable.complete()

        val commands = viewModel.commands()
        subscribeToPublishers(commands)

        viewModel.cancelGame()

        assertThat(commands.value).isEqualTo(WaitingRoomHostCommand.NavigateToHomeScreen)
    }

    private fun setupGameCanBeLaunchedMock(event: GameLaunchableEvent) {
        every { mockGameLaunchableUsecase.gameCanBeLaunched() } returns Observable.just(event)
    }
}