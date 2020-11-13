package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.host

import ch.qscqlmpa.dwitch.BaseViewModelUnitTest
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.HostCommunicationState
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.usecases.CancelGameUsecase
import ch.qscqlmpa.dwitch.ongoinggame.usecases.GameLaunchableEvent
import ch.qscqlmpa.dwitch.ongoinggame.usecases.GameLaunchableUsecase
import ch.qscqlmpa.dwitch.ongoinggame.usecases.LaunchGameUsecase
import ch.qscqlmpa.dwitch.scheduler.TestSchedulerFactory
import ch.qscqlmpa.dwitch.utils.DisposableManager
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.TestScheduler
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test

class WaitingRoomHostViewModelTest : BaseViewModelUnitTest() {

    private val mockCommunicator = mockk<HostCommunicator>(relaxed = true)

    private val mockGameLaunchableUsecase = mockk<GameLaunchableUsecase>(relaxed = true)

    private val mockCancelGameUsecase = mockk<CancelGameUsecase>(relaxed = true)

    private val mockLaunchGameUsecase = mockk<LaunchGameUsecase>(relaxed = true)

    private lateinit var viewModel: WaitingRoomHostViewModel

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
    }

    @After
    override fun tearDown() {
        super.tearDown()
    }

    @Test
    fun `Publish communication state`() {
        every { mockCommunicator.observeCommunicationState() } returns Observable.just(HostCommunicationState.ListeningForGuests)

        val currentCommunicationState = viewModel.currentCommunicationState()
        subscribeToPublishers(currentCommunicationState)

        assertThat(currentCommunicationState.value!!).isEqualTo(HostCommunicationState.ListeningForGuests)
        verify { mockCommunicator.observeCommunicationState() }
    }

    @Test
    fun `Publish that game can now be launched`() {
        setupGameCanBeLaunchedMock(GameLaunchableEvent.GameIsReadyToBeLaunched)

        val canGameBeLaunched = viewModel.canGameBeLaunched()
        subscribeToPublishers(canGameBeLaunched)

        assertThat(canGameBeLaunched.value).isEqualTo(true)
        verify { mockGameLaunchableUsecase.gameCanBeLaunched() }
        confirmVerified(mockGameLaunchableUsecase)
    }

    @Test
    fun `Publish that game cannot be launched because not all players are ready`() {
        setupGameCanBeLaunchedMock(GameLaunchableEvent.NotAllPlayersAreReady)

        val canGameBeLaunched = viewModel.canGameBeLaunched()
        subscribeToPublishers(canGameBeLaunched)

        assertThat(canGameBeLaunched.value).isEqualTo(false)
        verify { mockGameLaunchableUsecase.gameCanBeLaunched() }
        confirmVerified(mockGameLaunchableUsecase)
    }

    @Test
    fun `Publish that game cannot be launched because there is only one player in the room`() {
        setupGameCanBeLaunchedMock(GameLaunchableEvent.NotEnoughPlayers)

        val canGameBeLaunched = viewModel.canGameBeLaunched()
        subscribeToPublishers(canGameBeLaunched)

        assertThat(canGameBeLaunched.value).isEqualTo(false)
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
    fun `Publish NavigateToGameRoomScreen when game is launched`() {
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
    fun `Publish NavigateToGameHomeScreen when game is canceled`() {
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