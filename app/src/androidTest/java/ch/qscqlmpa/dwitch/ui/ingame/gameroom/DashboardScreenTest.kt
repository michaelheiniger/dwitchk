package ch.qscqlmpa.dwitch.ui.ingame.gameroom

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.performClick
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.assertTextIsDisplayedOnce
import ch.qscqlmpa.dwitch.base.BaseUiUnitTest
import ch.qscqlmpa.dwitch.ui.common.UiTags
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.game.PlayedCards
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerStatus
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank
import ch.qscqlmpa.dwitchgame.ingame.gameroom.PlayerInfo
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class DashboardScreenTest : BaseUiUnitTest() {

    private lateinit var dashboardInfo: DashboardInfo

    private var anyCardClicked = false
    private var playClicked = false
    private var passClicked = false

    @Before
    fun setup() {
        dashboardInfo = DashboardInfo(
            listOf(
                PlayerInfo("Aragorn", DwitchRank.President, DwitchPlayerStatus.Playing, dwitched = false, localPlayer = true),
                PlayerInfo("Boromir", DwitchRank.VicePresident, DwitchPlayerStatus.Done, dwitched = false, localPlayer = false),
                PlayerInfo("Gimli", DwitchRank.Neutral, DwitchPlayerStatus.Waiting, dwitched = false, localPlayer = false),
                PlayerInfo("Legolas", DwitchRank.Asshole, DwitchPlayerStatus.TurnPassed, dwitched = false, localPlayer = false),
                PlayerInfo("Galadriel", DwitchRank.ViceAsshole, DwitchPlayerStatus.Waiting, dwitched = true, localPlayer = false)
            ),
            LocalPlayerInfo(
                cardsInHand = listOf(
                    CardInfo(Card.Clubs2, selectable = true, selected = true),
                    CardInfo(Card.Clubs3, selectable = false, selected = false),
                    CardInfo(Card.Clubs4, selectable = false, selected = false),
                    CardInfo(Card.Clubs5, selectable = false, selected = false),
                    CardInfo(Card.Clubs6, selectable = false, selected = false),
                    CardInfo(Card.Clubs7, selectable = false, selected = false),
                    CardInfo(Card.Clubs8, selectable = true, selected = false),
                    CardInfo(Card.Clubs9, selectable = true, selected = false),
                    CardInfo(Card.Clubs10, selectable = true, selected = false),
                    CardInfo(Card.ClubsJack, selectable = true, selected = false)
                ),
                canPass = false,
                canPlay = true
            ),
            lastPlayerAction = null,
            lastCardOnTable = PlayedCards(Card.Hearts8),
            waitingForPlayerReconnection = false
        )
        anyCardClicked = false
        playClicked = false
        passClicked = false
    }

    @Test
    fun playersInfoIsDisplayed() {
        launchTest()

        composeTestRule.assertTextIsDisplayedOnce(
            textAsId = "Aragorn",
            containedStrings = arrayOf(getString(R.string.player_status_playing))
        )

        composeTestRule.assertTextIsDisplayedOnce(
            textAsId = "Boromir",
            containedStrings = arrayOf(getString(R.string.player_status_done))
        )

        composeTestRule.assertTextIsDisplayedOnce(
            textAsId = "Gimli",
            containedStrings = arrayOf(getString(R.string.player_status_waiting))
        )

        composeTestRule.assertTextIsDisplayedOnce(
            textAsId = "Legolas",
            containedStrings = arrayOf(getString(R.string.player_status_turnPassed))
        )

        composeTestRule.assertTextIsDisplayedOnce(
            textAsId = "Galadriel",
            containedStrings = arrayOf(getString(R.string.player_status_waiting))
        )
    }

    @Test
    fun lastCardPlayedIsDisplayed() {
        launchTest()

        composeTestRule.onNodeWithTag(UiTags.lastCardPlayed).assertIsDisplayed()
    }

    @Test
    fun cardsInHandAreDisplayed() {
        launchTest()

        composeTestRule.onNodeWithTag(Card.Clubs2.toString()).assertIsDisplayed()
        composeTestRule.onNodeWithTag(Card.Clubs3.toString()).assertIsDisplayed()
        composeTestRule.onNodeWithTag(Card.Clubs4.toString()).assertIsDisplayed()
        composeTestRule.onNodeWithTag(Card.Clubs5.toString()).assertIsDisplayed()
        composeTestRule.onNodeWithTag(Card.Clubs6.toString()).assertIsDisplayed()
        composeTestRule.onNodeWithTag(Card.Clubs7.toString()).assertIsDisplayed()
        composeTestRule.onNodeWithTag(Card.Clubs8.toString()).assertIsDisplayed()
        composeTestRule.onNodeWithTag(Card.Clubs9.toString()).assertIsDisplayed()
        composeTestRule.onNodeWithTag(Card.Clubs10.toString()).assertIsDisplayed()
        composeTestRule.onNodeWithTag(Card.ClubsJack.toString()).assertIsDisplayed()

        assertThat(anyCardClicked).isFalse
    }

    @Test
    fun cardsNotSelectableCannotBeClicked() {
        launchTest()

        composeTestRule.onNodeWithTag(Card.Clubs3.toString()).assertIsDisplayed().performClick()
        composeTestRule.onNodeWithTag(Card.Clubs4.toString()).assertIsDisplayed().performClick()
        composeTestRule.onNodeWithTag(Card.Clubs5.toString()).assertIsDisplayed().performClick()
        composeTestRule.onNodeWithTag(Card.Clubs6.toString()).assertIsDisplayed().performClick()
        composeTestRule.onNodeWithTag(Card.Clubs7.toString()).assertIsDisplayed().performClick()

        assertThat(anyCardClicked).isFalse
    }

    @Test
    fun cardsSelectableCanBeClicked() {
        launchTest()

        anyCardClicked = false
        composeTestRule.onNodeWithTag(Card.Clubs2.toString()).assertIsDisplayed().performClick()
        assertThat(anyCardClicked).isTrue

        anyCardClicked = false
        composeTestRule.onNodeWithTag(Card.Clubs8.toString()).assertIsDisplayed().performClick()
        assertThat(anyCardClicked).isTrue

        anyCardClicked = false
        composeTestRule.onNodeWithTag(Card.Clubs9.toString()).assertIsDisplayed().performClick()
        assertThat(anyCardClicked).isTrue

        anyCardClicked = false
        composeTestRule.onNodeWithTag(Card.Clubs10.toString()).assertIsDisplayed().performClick()
        assertThat(anyCardClicked).isTrue

        anyCardClicked = false
        composeTestRule.onNodeWithTag(Card.ClubsJack.toString()).assertIsDisplayed().performClick()
        assertThat(anyCardClicked).isTrue
    }

    @Test
    fun cannotPlayACardWhenControlIsDisabled() {
        dashboardInfo = dashboardInfo.copy(localPlayerInfo = dashboardInfo.localPlayerInfo.copy(canPlay = false))

        launchTest()

        composeTestRule.onNodeWithTag(UiTags.playCardControl).assertDoesNotExist()

        assertThat(playClicked).isFalse
    }

    @Test
    fun canPlayACardWhenControlIsEnabled() {
        dashboardInfo = dashboardInfo.copy(localPlayerInfo = dashboardInfo.localPlayerInfo.copy(canPlay = true))

        launchTest()

        composeTestRule.onNodeWithTag(UiTags.playCardControl).assertIsDisplayed().performClick()

        assertThat(playClicked).isTrue
    }

    @Test
    fun cannotPassWhenControlIsDisabled() {
        dashboardInfo = dashboardInfo.copy(localPlayerInfo = dashboardInfo.localPlayerInfo.copy(canPass = false))

        launchTest()

        composeTestRule.onNodeWithTag(UiTags.passTurnControl).assertDoesNotExist()

        assertThat(passClicked).isFalse
    }

    @Test
    fun canPassWhenControlIsEnabled() {
        dashboardInfo = dashboardInfo.copy(localPlayerInfo = dashboardInfo.localPlayerInfo.copy(canPass = true))

        launchTest()

        composeTestRule.onNodeWithTag(UiTags.passTurnControl).assertIsDisplayed().performClick()

        assertThat(passClicked).isTrue
    }

    @Test
    fun waitingDialogIsShownWhenWaitingForAnotherPlayerToReconnect() {
        dashboardInfo = dashboardInfo.copy(waitingForPlayerReconnection = true)

        launchTest()

        composeTestRule.onNodeWithTag(UiTags.waitingDialogAbortBtn).assertIsDisplayed()
    }

    private fun launchTest() {
        launchTestWithContent {
            Dashboard(
                dashboardInfo,
                onCardClick = { anyCardClicked = true },
                onPlayClick = { playClicked = true },
                onPassClick = { passClicked = true },
                onEndOrLeaveGameClick = {},
            )
        }
    }
}
