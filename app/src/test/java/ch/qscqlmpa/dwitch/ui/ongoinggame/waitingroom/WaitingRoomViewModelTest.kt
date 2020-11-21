package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom

import ch.qscqlmpa.dwitch.BaseViewModelUnitTest
import ch.qscqlmpa.dwitchcommonutil.DisposableManager
import ch.qscqlmpa.dwitchcommonutil.scheduler.TestSchedulerFactory
import ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom.PlayerWr
import ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom.WaitingRoomFacade
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.Observable
import io.reactivex.schedulers.TestScheduler
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class WaitingRoomViewModelTest : BaseViewModelUnitTest() {

    private val mockFacade = mockk<WaitingRoomFacade>(relaxed = true)

    private lateinit var viewModel: WaitingRoomViewModel

    private fun createViewModel() {
        val schedulerFactory = TestSchedulerFactory()
        schedulerFactory.setTimeScheduler(TestScheduler())
        viewModel = WaitingRoomViewModel(mockFacade, DisposableManager(), schedulerFactory)
    }

    @Test
    fun `publish connected players`() {
        val playerListRef = mockk<List<PlayerWr>>()
        every { mockFacade.observePlayers() } returns Observable.just(playerListRef)
        createViewModel()

        val connectedPlayers = viewModel.playersInWaitingRoom()
        subscribeToPublishers(connectedPlayers)

        assertThat(connectedPlayers.value!!).isEqualTo(playerListRef)
        verify { mockFacade.observePlayers() }
        confirmVerified(mockFacade)
    }
}