package ch.qscqlmpa.dwitch.ui.home

import ch.qscqlmpa.dwitch.app.AppEventRepository
import ch.qscqlmpa.dwitch.ingame.services.ServiceManager
import ch.qscqlmpa.dwitch.ui.BaseViewModelUnitTest
import ch.qscqlmpa.dwitch.ui.Destination
import ch.qscqlmpa.dwitch.ui.NavigationBridge
import ch.qscqlmpa.dwitch.ui.common.LoadedData
import ch.qscqlmpa.dwitch.ui.home.home.HomeViewModel
import ch.qscqlmpa.dwitchcommunication.GameAdvertisingInfo
import ch.qscqlmpa.dwitchgame.game.GameFacade
import ch.qscqlmpa.dwitchgame.gamediscovery.GameDiscoveryFacade
import ch.qscqlmpa.dwitchgame.gamelifecycle.GameLifecycleFacade
import ch.qscqlmpa.dwitchgame.gamelifecycle.GameLifecycleState
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchstore.model.ResumableGameInfo
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import org.assertj.core.api.Assertions.assertThat
import org.joda.time.DateTime
import org.joda.time.LocalDateTime
import org.junit.Before
import org.junit.Test
import java.util.*

class HomeViewModelTest : BaseViewModelUnitTest() {

    private val mockAppEventRepository = mockk<AppEventRepository>(relaxed = true)
    private val mockServiceManager = mockk<ServiceManager>(relaxed = true)
    private val mockGameDiscoveryFacade = mockk<GameDiscoveryFacade>(relaxed = true)
    private val mockGameLifecycleFacade = mockk<GameLifecycleFacade>(relaxed = true)
    private val mockGameFacade = mockk<GameFacade>(relaxed = true)
    private val mockNavigationBridge = mockk<NavigationBridge>(relaxed = true)

    private lateinit var viewModel: HomeViewModel

    private lateinit var advertisedGamesSubject: PublishSubject<List<GameAdvertisingInfo>>
    private lateinit var resumableGamesSubject: PublishSubject<List<ResumableGameInfo>>

    @Before
    fun setup() {
        viewModel = HomeViewModel(
            mockAppEventRepository,
            mockServiceManager,
            mockGameDiscoveryFacade,
            mockGameLifecycleFacade,
            mockGameFacade,
            mockNavigationBridge,
            Schedulers.trampoline()
        )

        advertisedGamesSubject = PublishSubject.create()
        every { mockGameDiscoveryFacade.observeAdvertisedGames() } returns advertisedGamesSubject

        resumableGamesSubject = PublishSubject.create()
        every { mockGameFacade.resumableGames() } returns resumableGamesSubject
    }

    @Test
    fun `should emit advertised games when successfully fetched`() {
        // Given
        viewModel.onStart()

        // When
        val list = listOf(
            GameAdvertisingInfo(
                true,
                "Kaamelott",
                GameCommonId(UUID.randomUUID()),
                "192.168.1.1",
                8890,
                LocalDateTime.now()
            )
        )
        advertisedGamesSubject.onNext(list)

        // Then
        assertThat((viewModel.advertisedGames.value as LoadedData.Success).data).isEqualTo(list)
    }

    @Test
    fun `should emit error when an error occurred while fetching advertised games`() {
        // Given
        viewModel.onStart()

        // When
        advertisedGamesSubject.onError(Exception())

        // Then
        assertThat(viewModel.advertisedGames.value).isInstanceOf(LoadedData.Failed::class.java)
    }

    @Test
    fun `should emit resumable games when successfully fetched`() {
        // Given
        viewModel.onStart()

        // When
        val list = listOf(
            ResumableGameInfo(
                1,
                DateTime.parse("2020-07-26T01:20+02:00"),
                "LOTR",
                listOf("Aragorn", "Legolas", "Gimli")
            ),
            ResumableGameInfo(
                2,
                DateTime.parse("2021-01-01T01:18+02:00"),
                "GoT",
                listOf("Ned Stark", "Arya Stark", "Sandor Clegane")
            )
        )
        resumableGamesSubject.onNext(list)

        // Then
        assertThat((viewModel.resumableGames.value as LoadedData.Success).data).isEqualTo(list)
    }

    @Test
    fun `should emit error when an error occurred while fetching resumable games`() {
        // Given
        viewModel.onStart()

        // When
        resumableGamesSubject.onError(Exception())

        // Then
        assertThat(viewModel.resumableGames.value).isInstanceOf(LoadedData.Failed::class.java)
    }

    @Test
    fun `should do nothing when no game is started`() {
        // Given
        every { mockGameLifecycleFacade.currentLifecycleState } returns GameLifecycleState.NotStarted

        // When
        viewModel.onStart()

        // Then
        verify(exactly = 0) { mockServiceManager.stop() }
        verify(exactly = 0) { mockNavigationBridge.navigate(any()) }
    }

    @Test
    fun `should navigate to InGame destination when a game is running`() {
        // Given
        every { mockGameLifecycleFacade.currentLifecycleState } returns GameLifecycleState.Running

        // When
        viewModel.onStart()

        // Then
        verify { mockNavigationBridge.navigate(Destination.HomeScreens.InGame) }
    }

    @Test
    fun `should stop service when game is over`() {
        // Given
        every { mockGameLifecycleFacade.currentLifecycleState } returns GameLifecycleState.Over

        // When
        viewModel.onStart()

        // Then
        verify { mockServiceManager.stop() }
        verify(exactly = 0) { mockNavigationBridge.navigate(any()) }
    }
}
