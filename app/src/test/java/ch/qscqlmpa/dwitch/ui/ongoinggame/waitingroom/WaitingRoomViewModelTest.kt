package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom

import ch.qscqlmpa.dwitch.BaseViewModelUnitTest
import ch.qscqlmpa.dwitchcommonutil.scheduler.TestSchedulerFactory
import ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom.WaitingRoomFacade
import ch.qscqlmpa.dwitchmodel.player.PlayerWr
import io.mockk.confirmVerified
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.schedulers.TestScheduler
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class WaitingRoomViewModelTest : BaseViewModelUnitTest() {

    private val mockFacade = mockk<WaitingRoomFacade>(relaxed = true)

    private lateinit var viewModel: WaitingRoomViewModel

    private fun createViewModel() {
        val schedulerFactory = TestSchedulerFactory()
        schedulerFactory.setTimeScheduler(TestScheduler())
        viewModel = WaitingRoomViewModel(mockFacade, Schedulers.trampoline())
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
