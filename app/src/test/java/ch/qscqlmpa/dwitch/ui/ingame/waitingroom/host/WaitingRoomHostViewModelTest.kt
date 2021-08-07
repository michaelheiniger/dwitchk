package ch.qscqlmpa.dwitch.ui.ingame.waitingroom.host

import ch.qscqlmpa.dwitch.app.ProdIdlingResource
import ch.qscqlmpa.dwitch.ui.BaseViewModelUnitTest
import ch.qscqlmpa.dwitchcommonutil.scheduler.TestSchedulerFactory
import ch.qscqlmpa.dwitchgame.ingame.usecases.GameLaunchableEvent
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.WaitingRoomHostFacade
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Completable
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

    private lateinit var canGameBeLaunchedSubject: PublishSubject<GameLaunchableEvent>

    @Before
    fun setup() {
        val schedulerFactory = TestSchedulerFactory()
        schedulerFactory.setTimeScheduler(TestScheduler())

        viewModel = WaitingRoomHostViewModel(mockFacade, Schedulers.trampoline(), ProdIdlingResource())

        canGameBeLaunchedSubject = PublishSubject.create()
        every { mockFacade.observeGameLaunchableEvents() } returns canGameBeLaunchedSubject
        viewModel.onStart()
    }

    @Test
    fun `Game cannot be launched initially`() {
        assertThat(viewModel.canGameBeLaunched.value).isFalse
    }

    @Test
    fun `Launch game control is enabled when game is ready to be launched`() {
        canGameBeLaunchedSubject.onNext(GameLaunchableEvent.GameIsReadyToBeLaunched)

        val canGameBeLaunched = viewModel.canGameBeLaunched

        assertThat(canGameBeLaunched.value).isTrue
        verify { mockFacade.observeGameLaunchableEvents() }
        confirmVerified(mockFacade)
    }

    @Test
    fun `Launch game control is disabled when not all players are ready`() {
        canGameBeLaunchedSubject.onNext(GameLaunchableEvent.NotAllPlayersAreReady)

        val canGameBeLaunched = viewModel.canGameBeLaunched

        assertThat(canGameBeLaunched.value).isFalse
        verify { mockFacade.observeGameLaunchableEvents() }
        confirmVerified(mockFacade)
    }

    @Test
    fun `Launch game control is disabled when there is only one player in the room`() {
        canGameBeLaunchedSubject.onNext(GameLaunchableEvent.NotEnoughPlayers)

        val canGameBeLaunched = viewModel.canGameBeLaunched

        assertThat(canGameBeLaunched.value).isFalse
        verify { mockFacade.observeGameLaunchableEvents() }
        confirmVerified(mockFacade)
    }

    @Test
    fun `Navigate to the GameRoom when the game is successfully launched`() {
        canGameBeLaunchedSubject.onNext(GameLaunchableEvent.GameIsReadyToBeLaunched)
        every { mockFacade.launchGame() } returns Completable.complete()

        viewModel.launchGame()

        assertThat(viewModel.navigation.value).isEqualTo(WaitingRoomHostDestination.NavigateToGameRoomScreen)

        verify { mockFacade.launchGame() }
        verify { mockFacade.observeGameLaunchableEvents() }
        confirmVerified(mockFacade)
    }

    @Test
    fun `Navigate Home when the game is canceled`() {
        every { mockFacade.cancelGame() } returns Completable.complete()

        val commands = viewModel.navigation

        viewModel.cancelGame()

        assertThat(commands.value).isEqualTo(WaitingRoomHostDestination.NavigateToHomeScreen)
        verify { mockFacade.cancelGame() }
        verify { mockFacade.observeGameLaunchableEvents() }
        confirmVerified(mockFacade)
    }
}
