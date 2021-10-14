package ch.qscqlmpa.dwitch.ui.ingame.waitingroom.host

import ch.qscqlmpa.dwitch.app.StubIdlingResource
import ch.qscqlmpa.dwitch.ui.BaseViewModelUnitTest
import ch.qscqlmpa.dwitch.ui.navigation.HomeDestination
import ch.qscqlmpa.dwitch.ui.navigation.InGameDestination
import ch.qscqlmpa.dwitch.ui.navigation.NavigationBridge
import ch.qscqlmpa.dwitchcommonutil.scheduler.TestSchedulerFactory
import ch.qscqlmpa.dwitchgame.gamediscovery.GameDiscoveryFacade
import ch.qscqlmpa.dwitchgame.ingame.gameadvertising.GameAdvertisingFacade
import ch.qscqlmpa.dwitchgame.ingame.usecases.GameLaunchableEvent
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.WaitingRoomHostFacade
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Completable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.schedulers.TestScheduler
import io.reactivex.rxjava3.subjects.PublishSubject
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class WaitingRoomHostViewModelTest : BaseViewModelUnitTest() {

    private val mockWaitingRoomHostFacade = mockk<WaitingRoomHostFacade>(relaxed = true)
    private val mockGameAdvertisingFacade = mockk<GameAdvertisingFacade>(relaxed = true)
    private val mockGameDiscoveryFacade = mockk<GameDiscoveryFacade>(relaxed = true)
    private val mockNavigationBridge = mockk<NavigationBridge>(relaxed = true)

    private lateinit var viewModel: WaitingRoomHostViewModel

    private lateinit var canGameBeLaunchedSubject: PublishSubject<GameLaunchableEvent>

    @Test
    fun `should prevent launching the game initially`() {
        // Given initial values (nothing to setup)

        // When
        createViewModel()
        viewModel.onStart()

        // Then
        assertThat(viewModel.canGameBeLaunched.value).isFalse
    }

    @Test
    fun `should allow launching the game when it becomes launchable`() {
        // Given
        createViewModel()
        viewModel.onStart()

        // When
        canGameBeLaunchedSubject.onNext(GameLaunchableEvent.GameIsReadyToBeLaunched)

        // Then
        assertThat(viewModel.canGameBeLaunched.value).isTrue
    }

    @Test
    fun `should prevent launching the game when not all players are ready`() {
        // Given
        createViewModel()
        viewModel.onStart()
        canGameBeLaunchedSubject.onNext(GameLaunchableEvent.GameIsReadyToBeLaunched)
        assertThat(viewModel.canGameBeLaunched.value).isTrue

        // When
        canGameBeLaunchedSubject.onNext(GameLaunchableEvent.NotAllPlayersAreReady)

        // Then
        assertThat(viewModel.canGameBeLaunched.value).isFalse
    }

    @Test
    fun `should prevent launching the game when there is not enough players in the game`() {
        // Given
        createViewModel()
        viewModel.onStart()
        canGameBeLaunchedSubject.onNext(GameLaunchableEvent.GameIsReadyToBeLaunched)
        assertThat(viewModel.canGameBeLaunched.value).isTrue

        // When
        canGameBeLaunchedSubject.onNext(GameLaunchableEvent.NotEnoughPlayers)

        // Then
        assertThat(viewModel.canGameBeLaunched.value).isFalse
    }

    @Test
    fun `should navigate to the GameRoom when the game is successfully launched`() {
        // Given
        createViewModel()
        viewModel.onStart()
        every { mockWaitingRoomHostFacade.launchGame() } returns Completable.complete()
        canGameBeLaunchedSubject.onNext(GameLaunchableEvent.GameIsReadyToBeLaunched)

        // When
        viewModel.launchGame()

        // Then
        verify { mockNavigationBridge.navigate(InGameDestination.GameRoomHost) }
        verify { mockWaitingRoomHostFacade.launchGame() }
    }

    @Test
    fun `should navigate to Home screen when the game is canceled`() {
        // Given
        createViewModel()
        viewModel.onStart()
        every { mockWaitingRoomHostFacade.cancelGame() } returns Completable.complete()

        // When
        viewModel.cancelGame()

        // Then
        verify { mockNavigationBridge.navigate(HomeDestination.Home) }
        verify { mockWaitingRoomHostFacade.cancelGame() }
    }

    private fun createViewModel() {
        val schedulerFactory = TestSchedulerFactory()
        schedulerFactory.setTimeScheduler(TestScheduler())

        viewModel = WaitingRoomHostViewModel(
            mockWaitingRoomHostFacade,
            mockGameDiscoveryFacade,
            mockGameAdvertisingFacade,
            mockNavigationBridge,
            Schedulers.trampoline(),
            StubIdlingResource()
        )

        canGameBeLaunchedSubject = PublishSubject.create()
        every { mockWaitingRoomHostFacade.observeGameLaunchableEvents() } returns canGameBeLaunchedSubject
    }
}
