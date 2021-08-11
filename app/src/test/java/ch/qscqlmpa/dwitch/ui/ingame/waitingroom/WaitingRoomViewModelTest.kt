package ch.qscqlmpa.dwitch.ui.ingame.waitingroom

import ch.qscqlmpa.dwitch.app.ProdIdlingResource
import ch.qscqlmpa.dwitch.ui.BaseViewModelUnitTest
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.GameInfoUi
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.PlayerWrUi
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.WaitingRoomFacade
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Single
import io.reactivex.rxjava3.schedulers.Schedulers
import io.reactivex.rxjava3.subjects.PublishSubject
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
class WaitingRoomViewModelTest : BaseViewModelUnitTest() {

    private val mockFacade = mockk<WaitingRoomFacade>(relaxed = true)

    private lateinit var viewModel: WaitingRoomViewModel

    private lateinit var playersSubject: PublishSubject<List<PlayerWrUi>>

    @Test
    fun `Provide list of players in the WaitingRoom`() {
        // Given
        createViewModel()
        assertThat(viewModel.players.value).isEqualTo(emptyList<List<PlayerWrUi>>())

        // When
        viewModel.onStart()
        val players1 = listOf<PlayerWrUi>(mockk())
        playersSubject.onNext(players1)

        // Then
        assertThat(viewModel.players.value).isEqualTo(players1)
        val players2 = listOf<PlayerWrUi>(mockk(), mockk())
        playersSubject.onNext(players2)
        assertThat(viewModel.players.value).isEqualTo(players2)

        // When
        viewModel.onStop()

        // Then the stream is no longer active so the players aren't updated
        val players3 = listOf<PlayerWrUi>(mockk(), mockk(), mockk())
        playersSubject.onNext(players3)
        assertThat(viewModel.players.value).isEqualTo(players2)
    }

    @Test
    fun `Computer players can be added when the game is a new one`() {
        // Given
        every { mockFacade.gameInfo() } returns Single.just(GameInfoUi("Dwiitch", gameIsNew = true))
        createViewModel()

        // When
        viewModel.onStart()

        // Then
        assertThat(viewModel.canComputerPlayersBeAdded.value).isTrue
    }

    @Test
    fun `Computer players cannot be added when the game is a resumed one`() {
        // Given
        every { mockFacade.gameInfo() } returns Single.just(GameInfoUi("Dwiitch", gameIsNew = false))
        createViewModel()

        // When
        viewModel.onStart()

        // Then
        assertThat(viewModel.canComputerPlayersBeAdded.value).isFalse
    }

    private fun createViewModel() {
        playersSubject = PublishSubject.create()
        every { mockFacade.observePlayers() } returns playersSubject
        viewModel = WaitingRoomViewModel(mockFacade, Schedulers.trampoline(), ProdIdlingResource())
    }
}
