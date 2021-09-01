package ch.qscqlmpa.dwitch.ui.ingame.waitingroom

import ch.qscqlmpa.dwitch.app.StubIdlingResource
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

class WaitingRoomViewModelTest : BaseViewModelUnitTest() {

    private val mockFacade = mockk<WaitingRoomFacade>(relaxed = true)

    private lateinit var viewModel: WaitingRoomViewModel

    private lateinit var playersSubject: PublishSubject<List<PlayerWrUi>>

    @Test
    fun `should expose the list of players in the WaitingRoom`() {
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
    }

    @Test
    fun `should dispose players stream on stop`() {
        // Given
        createViewModel()
        viewModel.onStart()
        val playersBefore = listOf<PlayerWrUi>(mockk(), mockk())
        playersSubject.onNext(playersBefore)

        // When
        viewModel.onStop()

        // Then the stream is no longer active so the players aren't updated
        val playersAfter = listOf<PlayerWrUi>(mockk(), mockk(), mockk())
        playersSubject.onNext(playersAfter)
        assertThat(viewModel.players.value).isEqualTo(playersBefore)
    }

    @Test
    fun `should allow adding computer players when the game is a new one`() {
        // Given
        every { mockFacade.gameInfo() } returns Single.just(GameInfoUi("Dwiitch", gameIsNew = true))

        // When
        createViewModel()

        // Then
        assertThat(viewModel.canComputerPlayersBeAdded.value).isTrue
    }

    @Test
    fun `should deny adding computer players when the game is an existing one`() {
        // Given
        every { mockFacade.gameInfo() } returns Single.just(GameInfoUi("Dwiitch", gameIsNew = false))

        // When
        createViewModel()

        // Then
        assertThat(viewModel.canComputerPlayersBeAdded.value).isFalse
    }

    private fun createViewModel() {
        playersSubject = PublishSubject.create()
        every { mockFacade.observePlayers() } returns playersSubject
        viewModel = WaitingRoomViewModel(mockFacade, Schedulers.trampoline(), StubIdlingResource())
    }
}
