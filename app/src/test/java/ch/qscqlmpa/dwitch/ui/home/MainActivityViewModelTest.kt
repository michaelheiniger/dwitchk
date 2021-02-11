package ch.qscqlmpa.dwitch.ui.home

import ch.qscqlmpa.dwitch.ui.BaseViewModelUnitTest
import ch.qscqlmpa.dwitch.ui.common.Status
import ch.qscqlmpa.dwitch.ui.home.main.MainActivityViewModel
import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitchgame.home.HomeGuestFacade
import ch.qscqlmpa.dwitchgame.home.HomeHostFacade
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import org.assertj.core.api.Assertions.assertThat
import org.joda.time.LocalTime
import org.junit.Before
import org.junit.Test

class MainActivityViewModelTest : BaseViewModelUnitTest() {

    private val mockHomeGuestFacade = mockk<HomeGuestFacade>(relaxed = true)
    private val mockHomeHostFacade = mockk<HomeHostFacade>(relaxed = true)

    private lateinit var viewModel: MainActivityViewModel

    private lateinit var gameRepositorySubject: PublishSubject<List<AdvertisedGame>>

    @Before
    override fun setup() {
        super.setup()

        viewModel = MainActivityViewModel(
            mockHomeGuestFacade,
            mockHomeHostFacade,
            Schedulers.trampoline()
        )

        gameRepositorySubject = PublishSubject.create()
        every { mockHomeGuestFacade.listenForAdvertisedGames() } returns gameRepositorySubject
    }

    @Test
    fun shouldEmitAdvertisedGameResponse_success() {
        val response = viewModel.observeAdvertisedGames()
        subscribeToPublishers(response)

        val list = listOf(AdvertisedGame(true, "Kaamelott", GameCommonId(1), "192.168.1.1", 8890, LocalTime.now()))
        gameRepositorySubject.onNext(list)

        assertThat(response.value!!.status).isEqualTo(Status.SUCCESS)
        assertThat(response.value!!.advertisedGames).isEqualTo(list)

        verify { mockHomeGuestFacade.listenForAdvertisedGames() }
    }

    @Test
    fun shouldEmitAdvertisedGameResponse_error() {
        gameRepositorySubject.onError(Exception())

        val response = viewModel.observeAdvertisedGames()
        subscribeToPublishers(response)

        assertThat(response.value!!.status).isEqualTo(Status.ERROR)
        assertThat(response.value!!.advertisedGames).isEqualTo(emptyList<AdvertisedGame>())

        verify { mockHomeGuestFacade.listenForAdvertisedGames() }
        verify { mockHomeGuestFacade.stopListeningForAdvertiseGames() } // Because stream terminates
    }

    @Test
    fun shouldUnsubscribeWhenStreamIsUnsubscribed() {
        val response = viewModel.observeAdvertisedGames()
        subscribeToPublishers(response)

        val list = listOf(AdvertisedGame(true, "Kaamelott", GameCommonId(1), "192.168.1.1", 8890, LocalTime.now()))
        gameRepositorySubject.onNext(list)

        unsubscribeFromPublishers()

        verify { mockHomeGuestFacade.listenForAdvertisedGames() }
        verify { mockHomeGuestFacade.stopListeningForAdvertiseGames() }
    }
}
