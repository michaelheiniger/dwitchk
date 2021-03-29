package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom

import ch.qscqlmpa.dwitch.ui.BaseViewModelUnitTest
import ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom.WaitingRoomFacade
import ch.qscqlmpa.dwitchmodel.player.PlayerWr
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class WaitingRoomViewModelTest : BaseViewModelUnitTest() {

    private val mockFacade = mockk<WaitingRoomFacade>(relaxed = true)

    private lateinit var viewModel: WaitingRoomViewModel

    private lateinit var playersSubject: PublishSubject<List<PlayerWr>>

    @Test
    fun `Observe players in the WaitingRoom after onStart has been called and before onStop is called`() {
        createViewModel()

        assertThat(viewModel.players.value).isEqualTo(emptyList<List<PlayerWr>>())

        viewModel.onStart()
        val players1 = mockk<List<PlayerWr>>()
        playersSubject.onNext(players1)

        assertThat(viewModel.players.value).isEqualTo(players1)

        val players2 = mockk<List<PlayerWr>>()
        playersSubject.onNext(players2)
        assertThat(viewModel.players.value).isEqualTo(players2)

        viewModel.onStop()

        // The stream is no longer active so the players aren't updated
        val players3 = mockk<List<PlayerWr>>()
        playersSubject.onNext(players3)
        assertThat(viewModel.players.value).isEqualTo(players2)
    }

    private fun createViewModel() {
        playersSubject = PublishSubject.create()
        every { mockFacade.observePlayers() } returns playersSubject
        viewModel = WaitingRoomViewModel(mockFacade, Schedulers.trampoline())
    }
}
