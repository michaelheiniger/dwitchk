package ch.qscqlmpa.dwitch.ui.home

import ch.qscqlmpa.dwitch.ui.BaseViewModelUnitTest
import ch.qscqlmpa.dwitch.ui.common.LoadedData
import ch.qscqlmpa.dwitch.ui.home.main.MainActivityViewModel
import ch.qscqlmpa.dwitchgame.appevent.AppEventRepository
import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame
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
import org.joda.time.LocalTime
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class) // Needed because of logging
class MainActivityViewModelTest : BaseViewModelUnitTest() {

    private val mockHomeGuestFacade = mockk<HomeGuestFacade>(relaxed = true)
    private val mockHomeHostFacade = mockk<HomeHostFacade>(relaxed = true)
    private val mockAppEventRepository = mockk<AppEventRepository>(relaxed = true)

    private lateinit var viewModel: MainActivityViewModel

    private lateinit var advertisedGamesSubject: PublishSubject<List<AdvertisedGame>>
    private lateinit var resumableGamesSubject: PublishSubject<List<ResumableGameInfo>>

    @Before
    fun setup() {
        viewModel = MainActivityViewModel(
            mockHomeGuestFacade,
            mockHomeHostFacade,
            mockAppEventRepository,
            Schedulers.trampoline()
        )

        advertisedGamesSubject = PublishSubject.create()
        every { mockHomeGuestFacade.listenForAdvertisedGames() } returns advertisedGamesSubject

        resumableGamesSubject = PublishSubject.create()
        every { mockHomeHostFacade.resumableGames() } returns resumableGamesSubject
    }

    @Test
    fun shouldEmitAdvertisedGamesSucceed() {
        viewModel.onStart()
        val data = viewModel.advertisedGames

        val list = listOf(AdvertisedGame(true, "Kaamelott", GameCommonId(1), "192.168.1.1", 8890, LocalTime.now()))
        advertisedGamesSubject.onNext(list)

        assertThat(data.value!!).isInstanceOf(LoadedData.Success::class.java)
        assertThat((data.value!! as LoadedData.Success).data).isEqualTo(list)

        verify { mockHomeGuestFacade.listenForAdvertisedGames() }
    }

    @Test
    fun shouldEmitLoadingAdvertisedGamesFailed() {
        viewModel.onStart()
        advertisedGamesSubject.onError(Exception())
        val response = viewModel.advertisedGames

        assertThat(response.value!!).isInstanceOf(LoadedData.Failed::class.java)

        verify { mockHomeGuestFacade.listenForAdvertisedGames() }
    }

    @Test
    fun shouldEmitLoadingResumableGamesSucceed() {
        viewModel.onStart()
        val data = viewModel.resumableGames

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

        assertThat(data.value!!).isInstanceOf(LoadedData.Success::class.java)
        assertThat((data.value!! as LoadedData.Success).data).isEqualTo(list)

        verify { mockHomeGuestFacade.listenForAdvertisedGames() }
    }

    @Test
    fun shouldEmitLoadingResumableGamesFailed() {
        viewModel.onStart()
        resumableGamesSubject.onError(Exception())
        val response = viewModel.resumableGames

        assertThat(response.value!!).isInstanceOf(LoadedData.Failed::class.java)

        verify { mockHomeHostFacade.resumableGames() }
    }
}
