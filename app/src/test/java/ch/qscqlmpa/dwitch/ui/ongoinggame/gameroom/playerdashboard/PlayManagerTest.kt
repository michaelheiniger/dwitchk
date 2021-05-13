package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom.playerdashboard

import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.info.DwitchCardInfo
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerStatus
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.*
import io.mockk.every
import io.mockk.mockk
import io.reactivex.rxjava3.subjects.PublishSubject
import org.assertj.core.api.Assertions
import org.junit.Before
import org.junit.Test

internal class PlayManagerTest {

    private val mockFacade = mockk<GameFacade>(relaxed = true)

    private lateinit var playManager: PlayManager

    private lateinit var gameDataSubject: PublishSubject<DwitchState>

    private val gameDashboardInfo = GameDashboardInfo(
        playersInfo = listOf(
            PlayerInfo("Aragorn", DwitchRank.President, DwitchPlayerStatus.Playing, dwitched = false, localPlayer = true),
            PlayerInfo("Haldir", DwitchRank.Asshole, DwitchPlayerStatus.Waiting, dwitched = false, localPlayer = false),
        ),
        localPlayerDashboard = LocalPlayerDashboard(
            cardsInHand = listOf(
                // Cards received are unsorted
                DwitchCardInfo(Card.Clubs3, selectable = false),
                DwitchCardInfo(Card.Clubs5, selectable = true),
                DwitchCardInfo(Card.Hearts2, selectable = true),
                DwitchCardInfo(Card.ClubsAce, selectable = true),
                DwitchCardInfo(Card.Spades2, selectable = true),
                DwitchCardInfo(Card.DiamondsJack, selectable = true),

                ),
            canPass = true
        ),
        lastCardPlayed = null
    )

    @Before
    fun setup() {
        gameDataSubject = PublishSubject.create()
        every { mockFacade.observeGameData() } returns gameDataSubject
        playManager = PlayManager(mockFacade)
    }

    @Test
    fun `Emit screen updates when round is beginning`() {
        // Given
        val screenObserver = playManager.observeScreen().test()

        // When
        gameDataSubject.onNext(DwitchState.RoundIsBeginning(gameDashboardInfo))

        // Then
        screenObserver.assertValueCount(1)
    }

    @Test
    fun `Emit screen updates when round is ongoing`() {
        // Given
        val screenObserver = playManager.observeScreen().test()

        // When
        gameDataSubject.onNext(DwitchState.RoundIsOngoing(gameDashboardInfo))

        // Then
        screenObserver.assertValueCount(1)
    }

    @Test
    fun `Cards in hand are sorted by value desc`() {
        // Given
        val screenObserver = playManager.observeScreen().test()

        // When
        gameDataSubject.onNext(DwitchState.RoundIsOngoing(gameDashboardInfo))

        // Then
        val cardsInHand = screenObserver.values()[0].dashboardInfo.localPlayerInfo.cardsInHand
        Assertions.assertThat(cardsInHand.size).isEqualTo(6)
        Assertions.assertThat(cardsInHand[0].card.value()).isEqualTo(2) // Joker (CardName.TWO) is considered with highest value
        Assertions.assertThat(cardsInHand[1].card.value()).isEqualTo(2) // Joker (CardName.TWO) is considered with highest value
        Assertions.assertThat(cardsInHand[2].card.value()).isEqualTo(14) // Ace
        Assertions.assertThat(cardsInHand[3].card.value()).isEqualTo(11) // Jack
        Assertions.assertThat(cardsInHand[4].card.value()).isEqualTo(5) // Five
        Assertions.assertThat(cardsInHand[5].card.value()).isEqualTo(3) // Three
    }

    @Test
    fun `Click on card updates the screen`() {
        // Given
        val screenObserver = playManager.observeScreen().test()
        gameDataSubject.onNext(DwitchState.RoundIsOngoing(gameDashboardInfo))

        // When
        playManager.onCardClick(Card.Clubs3)

        // Then
        screenObserver.assertValueCount(2)
    }
}