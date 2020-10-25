package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.host

import ch.qscqlmpa.dwitch.BaseViewModelUnitTest
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.HostCommunicationState
import ch.qscqlmpa.dwitch.ongoinggame.communication.host.HostCommunicator
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GameEvent
import ch.qscqlmpa.dwitch.ongoinggame.gameevent.GameEventRepository
import ch.qscqlmpa.dwitch.ongoinggame.usecases.CancelGameUsecase
import ch.qscqlmpa.dwitch.ongoinggame.usecases.GameLaunchableEvent
import ch.qscqlmpa.dwitch.ongoinggame.usecases.GameLaunchableUsecase
import ch.qscqlmpa.dwitch.ongoinggame.usecases.LaunchGameUsecase
import ch.qscqlmpa.dwitch.scheduler.TestSchedulerFactory
import ch.qscqlmpa.dwitch.utils.DisposableManager
import io.mockk.*
import io.reactivex.Completable
import io.reactivex.Observable
import io.reactivex.schedulers.TestScheduler
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test

class WaitingRoomHostViewModelTest : BaseViewModelUnitTest() {

    private val mockCommunicator = mockk<HostCommunicator>()

    private val mockGameLaunchableUsecase = mockk<GameLaunchableUsecase>()

    private val mockCancelGameUsecase = mockk<CancelGameUsecase>()

    private val mockLaunchGameUsecase = mockk<LaunchGameUsecase>()

    private val mockGameEventRepository = mockk<GameEventRepository>(relaxed = true)

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
            mockGameEventRepository,
            DisposableManager(),
            schedulerFactory
        )
    }

    @After
    override fun tearDown() {
        super.tearDown()
        clearMocks(
            mockCommunicator,
            mockGameLaunchableUsecase,
            mockCancelGameUsecase,
            mockLaunchGameUsecase,
            mockGameEventRepository
        )
    }

    @Test
    fun `Publish communication state`() {

        every { mockCommunicator.observeCommunicationState() } returns Observable.just(
            HostCommunicationState.LISTENING_FOR_GUESTS
        )

        val currentCommunicationState = viewModel.currentCommunicationState()
        subscribeToPublishers(currentCommunicationState)

        assertThat(currentCommunicationState.value!!.id).isEqualTo(R.string.listening_for_guests)
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
    fun `Publish NavigateToGameRoomScreen when GameLaunched event occurs`() {
        every { mockGameEventRepository.observeEvents() } returns Observable.just(GameEvent.GameLaunched)

        val commands = viewModel.commands()
        subscribeToPublishers(commands)

        assertThat(commands.value).isEqualTo(WaitingRoomHostCommand.NavigateToGameRoomScreen)
    }

    private fun setupGameCanBeLaunchedMock(event: GameLaunchableEvent) {
        every { mockGameLaunchableUsecase.gameCanBeLaunched() } returns Observable.just(event)
    }
}