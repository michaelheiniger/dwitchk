package ch.qscqlmpa.dwitch.ui.ingame.waitingroom.host

import ch.qscqlmpa.dwitch.app.StubIdlingResource
import ch.qscqlmpa.dwitch.ui.BaseViewModelUnitTest
import ch.qscqlmpa.dwitch.ui.Destination
import ch.qscqlmpa.dwitch.ui.NavigationBridge
import ch.qscqlmpa.dwitchcommonutil.scheduler.TestSchedulerFactory
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

    private val mockFacade = mockk<WaitingRoomHostFacade>(relaxed = true)
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
        every { mockFacade.launchGame() } returns Completable.complete()
        canGameBeLaunchedSubject.onNext(GameLaunchableEvent.GameIsReadyToBeLaunched)

        // When
        viewModel.launchGame()

        // Then
        verify { mockNavigationBridge.navigate(Destination.GameScreens.GameRoomHost) }
        verify { mockFacade.launchGame() }
    }

    @Test
    fun `should navigate to Home screen when the game is canceled`() {
        // Given
        createViewModel()
        viewModel.onStart()
        every { mockFacade.cancelGame() } returns Completable.complete()

        // When
        viewModel.cancelGame()

        // Then
        verify { mockNavigationBridge.navigate(Destination.HomeScreens.Home) }
        verify { mockFacade.cancelGame() }
    }

    private fun createViewModel() {
        val schedulerFactory = TestSchedulerFactory()
        schedulerFactory.setTimeScheduler(TestScheduler())

        viewModel = WaitingRoomHostViewModel(
            mockFacade,
            gameAdvertisingFacade = mockk(relaxed = true),
            mockNavigationBridge,
            Schedulers.trampoline(),
            StubIdlingResource()
        )

        canGameBeLaunchedSubject = PublishSubject.create()
        every { mockFacade.observeGameLaunchableEvents() } returns canGameBeLaunchedSubject
    }
}
