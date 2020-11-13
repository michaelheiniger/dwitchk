package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom

import ch.qscqlmpa.dwitch.BaseViewModelUnitTest
import ch.qscqlmpa.dwitch.ongoinggame.communication.waitingroom.PlayerWr
import ch.qscqlmpa.dwitch.ongoinggame.communication.waitingroom.WaitingRoomPlayerRepository
import ch.qscqlmpa.dwitch.scheduler.TestSchedulerFactory
import ch.qscqlmpa.dwitch.utils.DisposableManager
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Observable
import io.reactivex.schedulers.TestScheduler
import org.assertj.core.api.Assertions.assertThat
import org.junit.After
import org.junit.Before
import org.junit.Test

class WaitingRoomViewModelTest : BaseViewModelUnitTest() {

    private val mockPlayerWrRepository = mockk<WaitingRoomPlayerRepository>(relaxed = true)

    private lateinit var viewModel: WaitingRoomViewModel

    @Before
    override fun setup() {
        super.setup()
    }

    @After
    override fun tearDown() {
        super.tearDown()
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
        every { mockPlayerWrRepository.observePlayers() } returns Observable.just(playerListRef)
        createViewModel()

        val connectedPlayers = viewModel.playersInWaitingRoom()
        subscribeToPublishers(connectedPlayers)

        assertThat(connectedPlayers.value!!).isEqualTo(playerListRef)
        verify { mockPlayerWrRepository.observePlayers() }
        confirmVerified(mockPlayerWrRepository)
    }
}