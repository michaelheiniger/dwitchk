package ch.qscqlmpa.dwitch.ui.ingame.gameroom.playerdashboard

import ch.qscqlmpa.dwitch.ui.ingame.gameroom.CardInfo
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.card.CardName
import ch.qscqlmpa.dwitchengine.model.game.PlayedCards
import ch.qscqlmpa.dwitchengine.model.info.DwitchCardInfo
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerStatus
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank
import ch.qscqlmpa.dwitchgame.ingame.gameroom.GameDashboardInfo
import ch.qscqlmpa.dwitchgame.ingame.gameroom.LocalPlayerDashboard
import ch.qscqlmpa.dwitchgame.ingame.gameroom.PlayerInfo
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner

@RunWith(RobolectricTestRunner::class)
internal class DashboardScreenBuilderTest {

    private lateinit var dashboardScreenBuilder: DashboardScreenBuilder

    private val gameDashboardInfo = GameDashboardInfo(
        playersInfo = listOf(
            PlayerInfo("Aragorn", DwitchRank.President, DwitchPlayerStatus.Playing, dwitched = false, localPlayer = true),
            PlayerInfo("Haldir", DwitchRank.Asshole, DwitchPlayerStatus.Waiting, dwitched = false, localPlayer = false)
        ),
        localPlayerDashboard = LocalPlayerDashboard(
            cardsInHand = listOf(
                // Cards received are unsorted
                DwitchCardInfo(Card.Clubs3, selectable = false),
                DwitchCardInfo(Card.Clubs5, selectable = true),
                DwitchCardInfo(Card.Hearts2, selectable = true),
                DwitchCardInfo(Card.ClubsAce, selectable = true),
                DwitchCardInfo(Card.Spades2, selectable = true),
                DwitchCardInfo(Card.DiamondsJack, selectable = true)
            ),
            canPass = true
        ),
        lastCardPlayed = PlayedCards(Card.Clubs4),
        waitingForPlayerReconnection = false
    )

    @Before
    fun setup() {
        dashboardScreenBuilder = DashboardScreenBuilder(gameDashboardInfo)
    }

    @Test
    fun `Initial screen is generated according to game dashboard info`() {
        // Given initial game dashboard info
        // Then the screen is properly built
        val dashboardInfo = dashboardScreenBuilder.screen.dashboardInfo
        assertThat(dashboardInfo.lastCardPlayed).isEqualTo(PlayedCards(Card.Clubs4))
        assertThat(dashboardInfo.waitingForPlayerReconnection).isFalse
        assertThat(dashboardInfo.localPlayerInfo.canPlay).isFalse
        assertThat(dashboardInfo.localPlayerInfo.canPass).isTrue
        assertThat(dashboardInfo.localPlayerInfo.cardsInHand).containsExactlyInAnyOrder(
            CardInfo(Card.Clubs3, selectable = false, selected = false),
            CardInfo(Card.Clubs5, selectable = true, selected = false),
            CardInfo(Card.Hearts2, selectable = true, selected = false),
            CardInfo(Card.ClubsAce, selectable = true, selected = false),
            CardInfo(Card.Spades2, selectable = true, selected = false),
            CardInfo(Card.DiamondsJack, selectable = true, selected = false)
        )
        assertThat(dashboardInfo.playersInfo.size).isEqualTo(2)

        assertThat(dashboardInfo.playersInfo[0].name).isEqualTo("Aragorn")
        assertThat(dashboardInfo.playersInfo[0].dwitched).isFalse
        assertThat(dashboardInfo.playersInfo[0].localPlayer).isTrue
        assertThat(dashboardInfo.playersInfo[0].rank).isEqualTo(DwitchRank.President)
        assertThat(dashboardInfo.playersInfo[0].status).isEqualTo(DwitchPlayerStatus.Playing)

        assertThat(dashboardInfo.playersInfo[1].name).isEqualTo("Haldir")
        assertThat(dashboardInfo.playersInfo[1].dwitched).isFalse
        assertThat(dashboardInfo.playersInfo[1].localPlayer).isFalse
        assertThat(dashboardInfo.playersInfo[1].rank).isEqualTo(DwitchRank.Asshole)
        assertThat(dashboardInfo.playersInfo[1].status).isEqualTo(DwitchPlayerStatus.Waiting)
    }

    @Test
    fun `Cards in hand are sorted by value desc`() {
        // Given unsorted cards
        assertThat(gameDashboardInfo.localPlayerDashboard.cardsInHand[0].card).isEqualTo(Card.Clubs3)
        assertThat(gameDashboardInfo.localPlayerDashboard.cardsInHand[1].card).isEqualTo(Card.Clubs5)

        // When querying the screen, then the cards are sorted
        val cardsInHand = dashboardScreenBuilder.screen.dashboardInfo.localPlayerInfo.cardsInHand

        // Then the cards are sorted
        assertThat(cardsInHand.size).isEqualTo(6)
        assertThat(cardsInHand[0].card.name).isEqualTo(CardName.Two) // Joker is considered having the highest value
        assertThat(cardsInHand[1].card.name).isEqualTo(CardName.Two) // Joker (CardName.TWO) is considered having the highest value
        assertThat(cardsInHand[2].card.name).isEqualTo(CardName.Ace)
        assertThat(cardsInHand[3].card.name).isEqualTo(CardName.Jack)
        assertThat(cardsInHand[4].card.name).isEqualTo(CardName.Five)
        assertThat(cardsInHand[5].card.name).isEqualTo(CardName.Three)
    }

    @Test
    fun `Click on card updates the screen`() {
        // Given no card is selected
        assertThat(dashboardScreenBuilder.screen.dashboardInfo.localPlayerInfo.cardsInHand).noneMatch { c -> c.selected }

        // When a non-selected card is clicked
        val clickedCard = Card.Clubs5
        dashboardScreenBuilder.onCardClick(clickedCard)

        // Then it becomes selected
        assertThat(dashboardScreenBuilder.screen.dashboardInfo.localPlayerInfo.cardsInHand.filter { c -> c.card != clickedCard }).noneMatch { c -> c.selected }
        assertThat(dashboardScreenBuilder.screen.dashboardInfo.localPlayerInfo.cardsInHand.filter { c -> c.card == clickedCard }).allMatch { c -> c.selected }
    }
}
