package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.playerdashboard

import ch.qscqlmpa.dwitch.ui.BaseViewModelUnitTest
import ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.guest.GameRoomScreen
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.info.DwitchCardInfo
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.DwitchState
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.GameDashboardInfo
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.GameFacade
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.LocalPlayerDashboard
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.core.Observable
import io.reactivex.rxjava3.schedulers.Schedulers
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class) // Needed because of logging
class PlayerDashboardViewModelTest : BaseViewModelUnitTest() {

    private val mockGameFacade = mockk<GameFacade>(relaxed = true)

    private lateinit var viewModel: PlayerDashboardViewModel

    override fun setup() {
        super.setup()

        viewModel = PlayerDashboardViewModel(mockGameFacade, Schedulers.trampoline())
    }

    @Test
    fun cardsInHandShouldBeSortedByValueDesc() {
        val dwitchState = DwitchState.RoundIsOngoing(
            GameDashboardInfo(
                emptyList(), // Irrelevant
                localPlayerDashboard = LocalPlayerDashboard(
                    listOf(
                        // Cards are received unsorted
                        DwitchCardInfo(Card.Clubs3, selectable = false),
                        DwitchCardInfo(Card.Clubs5, selectable = true),
                        DwitchCardInfo(Card.Hearts2, selectable = true),
                        DwitchCardInfo(Card.ClubsAce, selectable = true),
                        DwitchCardInfo(Card.Spades2, selectable = true),
                        DwitchCardInfo(Card.DiamondsJack, selectable = true),
                    ),
                    canPlay = true
                ),
                lastCardPlayed = Card.Clubs4
            )
        )

        every { mockGameFacade.observeGameData() } returns Observable.just(dwitchState)

        val cardsInHand = (viewModel.screen.value as GameRoomScreen.Dashboard).dashboardInfo.localPlayerDashboard.cardsInHand
        assertThat(cardsInHand.size).isEqualTo(6)
        assertThat(cardsInHand[0].card.value()).isEqualTo(2) // Joker (CardName.TWO) is considered with highest value
        assertThat(cardsInHand[1].card.value()).isEqualTo(2) // Joker (CardName.TWO) is considered with highest value
        assertThat(cardsInHand[2].card.value()).isEqualTo(14) // Ace
        assertThat(cardsInHand[3].card.value()).isEqualTo(11) // Jack
        assertThat(cardsInHand[4].card.value()).isEqualTo(5) // Five
        assertThat(cardsInHand[5].card.value()).isEqualTo(3) // Three
    }
}
