package ch.qscqlmpa.dwitch.ui.home

import ch.qscqlmpa.dwitch.BaseViewModelUnitTest
import ch.qscqlmpa.dwitch.gamediscovery.AdvertisedGame
import ch.qscqlmpa.dwitch.gamediscovery.AdvertisedGameRepository
import ch.qscqlmpa.dwitch.scheduler.TestSchedulerFactory
import ch.qscqlmpa.dwitch.ui.common.Status
import ch.qscqlmpa.dwitch.ui.home.main.MainActivityViewModel
import ch.qscqlmpa.dwitch.utils.DisposableManager
import io.mockk.*
import io.reactivex.Observable
import io.reactivex.schedulers.TestScheduler
import org.joda.time.LocalTime
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test

class MainActivityViewModelTest : BaseViewModelUnitTest() {

    private val mockGameRepository = mockk<AdvertisedGameRepository>()

    private lateinit var viewModel: MainActivityViewModel

    @Before
    override fun setup() {
        super.setup()

        val schedulerFactory = TestSchedulerFactory()
        schedulerFactory.setTimeScheduler(TestScheduler())
        viewModel = MainActivityViewModel(mockGameRepository, DisposableManager(), schedulerFactory)
    }

    @After
    override fun tearDown() {
        super.tearDown()
        clearMocks(mockGameRepository)
    }

    @Test
    fun shouldEmitAdvertisedGameResponse_success() {

        val list = listOf(AdvertisedGame("Kaamelott", "192.168.1.1", 8890, LocalTime.now()))
        every { mockGameRepository.listenForAdvertisedGames() } returns Observable.just(list)

        val response = viewModel.observeAdvertisedGames().value

        assertEquals(Status.SUCCESS, response!!.status)
        assertEquals(list, response.advertisedGames)

        verify { mockGameRepository.listenForAdvertisedGames() }

        confirmVerified(mockGameRepository)
    }

    @Test
    fun shouldEmitAdvertisedGameResponse_error() {

        every { mockGameRepository.listenForAdvertisedGames() } returns Observable.error(Exception())

        val response = viewModel.observeAdvertisedGames().value

        assertEquals(Status.ERROR, response!!.status)
        assertEquals(emptyList<AdvertisedGame>(), response.advertisedGames)

        verify { mockGameRepository.listenForAdvertisedGames() }

        confirmVerified(mockGameRepository)
    }
}