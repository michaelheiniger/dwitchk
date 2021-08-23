package ch.qscqlmpa.dwitch.ui.home

import ch.qscqlmpa.dwitch.app.AppEventRepository
import ch.qscqlmpa.dwitch.ui.BaseViewModelUnitTest
import ch.qscqlmpa.dwitch.ui.common.LoadedData
import ch.qscqlmpa.dwitch.ui.home.home.HomeScreenViewModel
import ch.qscqlmpa.dwitchgame.common.GameAdvertisingFacade
import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitchgame.home.HomeFacade
import ch.qscqlmpa.dwitchgame.home.HomeGuestFacade
import ch.qscqlmpa.dwitchgame.home.HomeHostFacade
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
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@Config(manifest = Config.NONE) // Prevent missing AndroidManifest log
@RunWith(RobolectricTestRunner::class) // Needed because of logging
class HomeScreenViewModelTest : BaseViewModelUnitTest() {

    private val mockAppEventRepository = mockk<AppEventRepository>(relaxed = true)
    private val mockGameAdvertisingFacade = mockk<GameAdvertisingFacade>(relaxed = true)
    private val mockHomeFacade = mockk<HomeFacade>(relaxed = true)
    private val mockHomeGuestFacade = mockk<HomeGuestFacade>(relaxed = true)
    private val mockHomeHostFacade = mockk<HomeHostFacade>(relaxed = true)

    private lateinit var screenViewModel: HomeScreenViewModel

    private lateinit var advertisedGamesSubject: PublishSubject<List<AdvertisedGame>>
    private lateinit var resumableGamesSubject: PublishSubject<List<ResumableGameInfo>>

    @Before
    fun setup() {
        screenViewModel = HomeScreenViewModel(
            mockAppEventRepository,
            mockGameAdvertisingFacade,
            mockHomeFacade,
            mockHomeGuestFacade,
            mockHomeHostFacade,
            Schedulers.trampoline()
        )

        advertisedGamesSubject = PublishSubject.create()
        every { mockGameAdvertisingFacade.observeAdvertisedGames() } returns advertisedGamesSubject

        resumableGamesSubject = PublishSubject.create()
        every { mockHomeHostFacade.resumableGames() } returns resumableGamesSubject
    }

    @Test
    fun `should emit advertised games - succeed`() {
        screenViewModel.onStart()
        val data = screenViewModel.advertisedGames

        val list = listOf(AdvertisedGame(true, "Kaamelott", GameCommonId(1), "192.168.1.1", 8890, LocalDateTime.now()))
        advertisedGamesSubject.onNext(list)

        assertThat(data.value).isInstanceOf(LoadedData.Success::class.java)
        assertThat((data.value as LoadedData.Success).data).isEqualTo(list)

        verify { mockGameAdvertisingFacade.observeAdvertisedGames() }
    }

    @Test
    fun `should emit loading advertised games - failed`() {
        screenViewModel.onStart()
        advertisedGamesSubject.onError(Exception())
        val response = screenViewModel.advertisedGames

        assertThat(response.value).isInstanceOf(LoadedData.Failed::class.java)

        verify { mockGameAdvertisingFacade.observeAdvertisedGames() }
    }

    @Test
    fun `should emit loading resumable games - succeed`() {
        screenViewModel.onStart()
        val data = screenViewModel.resumableGames

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

        assertThat(data.value).isInstanceOf(LoadedData.Success::class.java)
        assertThat((data.value as LoadedData.Success).data).isEqualTo(list)

        verify { mockGameAdvertisingFacade.observeAdvertisedGames() }
    }

    @Test
    fun `should emit loading resumable games - failed`() {
        screenViewModel.onStart()
        resumableGamesSubject.onError(Exception())
        val response = screenViewModel.resumableGames

        assertThat(response.value).isInstanceOf(LoadedData.Failed::class.java)

        verify { mockHomeHostFacade.resumableGames() }
    }
}
