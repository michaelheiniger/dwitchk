package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom

import ch.qscqlmpa.dwitch.BaseViewModelUnitTest
import ch.qscqlmpa.dwitch.ongoinggame.communication.waitingroom.PlayerWr
import ch.qscqlmpa.dwitch.ongoinggame.communication.waitingroom.PlayerWrRepository
import ch.qscqlmpa.dwitch.scheduler.TestSchedulerFactory
import ch.qscqlmpa.dwitch.utils.DisposableManager
import io.mockk.*
import io.reactivex.Observable
import io.reactivex.schedulers.TestScheduler
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test

class WaitingRoomViewModelTest : BaseViewModelUnitTest() {

    private val mockPlayerWrRepository = mockk<PlayerWrRepository>(relaxed = true)

    private lateinit var viewModel: WaitingRoomViewModel

    @Before
    override fun setup() {
        super.setup()
    }

    @After
    override fun tearDown() {
        super.tearDown()
        clearMocks(mockPlayerWrRepository)
    }

    private fun createViewModel() {
        val schedulerFactory = TestSchedulerFactory()
        schedulerFactory.setTimeScheduler(TestScheduler())
        viewModel = WaitingRoomViewModel(
                mockPlayerWrRepository,
                DisposableManager(),
                schedulerFactory
        )
    }

    @Test
    fun `publish connected players`() {
        val playerListRef = mockk<List<PlayerWr>>()
        every { mockPlayerWrRepository.observeConnectedPlayers() } returns Observable.just(playerListRef)
        createViewModel()

        val connectedPlayers = viewModel.connectedPlayers()
        subscribeToPublishers(connectedPlayers)

        assertThat(connectedPlayers.value!!).isEqualTo(playerListRef)
        verify { mockPlayerWrRepository.observeConnectedPlayers() }
        confirmVerified(mockPlayerWrRepository)
    }
}