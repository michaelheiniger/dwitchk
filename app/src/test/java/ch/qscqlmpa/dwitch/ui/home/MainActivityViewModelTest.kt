package ch.qscqlmpa.dwitch.ui.home

import ch.qscqlmpa.dwitch.BaseViewModelUnitTest
import ch.qscqlmpa.dwitch.ui.common.Status
import ch.qscqlmpa.dwitch.ui.home.main.MainActivityViewModel
import ch.qscqlmpa.dwitchcommonutil.DisposableManager
import ch.qscqlmpa.dwitchcommonutil.scheduler.TestSchedulerFactory
import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitchgame.gamediscovery.AdvertisedGameRepository
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.schedulers.TestScheduler
import io.reactivex.subjects.PublishSubject
import org.assertj.core.api.Assertions.assertThat
import org.joda.time.LocalTime
import org.junit.Before
import org.junit.Test

class MainActivityViewModelTest : BaseViewModelUnitTest() {

    private val mockGameRepository = mockk<AdvertisedGameRepository>(relaxed = true)

    private lateinit var viewModel: MainActivityViewModel

    private lateinit var gameRepositorySubject: PublishSubject<List<AdvertisedGame>>

    @Before
    override fun setup() {
        super.setup()

        val schedulerFactory = TestSchedulerFactory()
        schedulerFactory.setTimeScheduler(TestScheduler())
        viewModel = MainActivityViewModel(mockGameRepository, DisposableManager(), schedulerFactory)

        gameRepositorySubject = PublishSubject.create()
        every { mockGameRepository.listenForAdvertisedGames() } returns gameRepositorySubject
    }

    @Test
    fun shouldEmitAdvertisedGameResponse_success() {
        val response = viewModel.observeAdvertisedGames()
        subscribeToPublishers(response)

        val list = listOf(AdvertisedGame("Kaamelott", GameCommonId(1), "192.168.1.1", 8890, LocalTime.now()))
        gameRepositorySubject.onNext(list)

        assertThat(response.value!!.status).isEqualTo(Status.SUCCESS)
        assertThat(response.value!!.advertisedGames).isEqualTo(list)

        verify { mockGameRepository.listenForAdvertisedGames() }

        confirmVerified(mockGameRepository)
    }

    @Test
    fun shouldEmitAdvertisedGameResponse_error() {
        gameRepositorySubject.onError(Exception())

        val response = viewModel.observeAdvertisedGames()
        subscribeToPublishers(response)

        assertThat(response.value!!.status).isEqualTo(Status.ERROR)
        assertThat(response.value!!.advertisedGames).isEqualTo(emptyList<AdvertisedGame>())

        verify { mockGameRepository.listenForAdvertisedGames() }
        verify { mockGameRepository.stopListening() } // Because stream terminates

        confirmVerified(mockGameRepository)
    }

    @Test
    fun shouldUnsubscribeWhenStreamIsUnsubscribed() {
        val response = viewModel.observeAdvertisedGames()
        subscribeToPublishers(response)

        val list = listOf(AdvertisedGame("Kaamelott", GameCommonId(1), "192.168.1.1", 8890, LocalTime.now()))
        gameRepositorySubject.onNext(list)

        unsubscribeFromPublishers()

        verify { mockGameRepository.listenForAdvertisedGames() }
        verify { mockGameRepository.stopListening() }

        confirmVerified(mockGameRepository)
    }
}