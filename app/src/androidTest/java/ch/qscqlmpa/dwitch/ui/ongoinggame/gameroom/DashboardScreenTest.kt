package ch.qscqlmpa.dwitch.ui.ongoinggame.gameroom

import DashboardScreen
import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.hasTestTag
import androidx.compose.ui.test.performClick
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.assertTextIsDisplayedOnce
import ch.qscqlmpa.dwitch.base.BaseUiUnitTest
import ch.qscqlmpa.dwitchengine.model.card.Card
import ch.qscqlmpa.dwitchengine.model.info.DwitchCardInfo
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerStatus
import ch.qscqlmpa.dwitchengine.model.player.DwitchRank
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.GameDashboardInfo
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.LocalPlayerDashboard
import ch.qscqlmpa.dwitchgame.ongoinggame.gameroom.PlayerInfo
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class DashboardScreenTest : BaseUiUnitTest() {

    private lateinit var dashboardInfo: GameDashboardInfo

    private var anyCardClicked = false
    private var pickCardClicked = false
    private var passClicked = false

    @Before
    fun setup() {
        dashboardInfo = GameDashboardInfo(
            listOf(
                PlayerInfo("Aragorn", DwitchRank.President, DwitchPlayerStatus.Playing, dwitched = false, localPlayer = true),
                PlayerInfo("Boromir", DwitchRank.VicePresident, DwitchPlayerStatus.Done, dwitched = false, localPlayer = false),
                PlayerInfo("Gimli", DwitchRank.Neutral, DwitchPlayerStatus.Waiting, dwitched = false, localPlayer = false),
                PlayerInfo("Legolas", DwitchRank.Asshole, DwitchPlayerStatus.TurnPassed, dwitched = false, localPlayer = false),
                PlayerInfo("Galadriel", DwitchRank.ViceAsshole, DwitchPlayerStatus.Waiting, dwitched = true, localPlayer = false)
            ),
            LocalPlayerDashboard(
                cardsInHand = listOf(
                    DwitchCardInfo(Card.Clubs2, true),
                    DwitchCardInfo(Card.Clubs3, false),
                    DwitchCardInfo(Card.Clubs4, false),
                    DwitchCardInfo(Card.Clubs5, false),
                    DwitchCardInfo(Card.Clubs6, false),
                    DwitchCardInfo(Card.Clubs7, false),
                    DwitchCardInfo(Card.Clubs8, true),
                    DwitchCardInfo(Card.Clubs9, true),
                    DwitchCardInfo(Card.Clubs10, true),
                    DwitchCardInfo(Card.ClubsJack, true),
                ),
                canPass = false,
                canPickACard = true,
                canPlay = true
            ),
            lastCardPlayed = Card.Hearts8
        )
        anyCardClicked = false
        pickCardClicked = false
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

        composeTestRule.onNode(hasTestTag("lastCardPlayed")).assertIsDisplayed()
    }

    @Test
    fun cardsInHandAreDisplayed() {
        launchTest()

        composeTestRule.onNode(hasTestTag(Card.Clubs2.toString())).assertIsDisplayed()
        composeTestRule.onNode(hasTestTag(Card.Clubs3.toString())).assertIsDisplayed()
        composeTestRule.onNode(hasTestTag(Card.Clubs4.toString())).assertIsDisplayed()
        composeTestRule.onNode(hasTestTag(Card.Clubs5.toString())).assertIsDisplayed()
        composeTestRule.onNode(hasTestTag(Card.Clubs6.toString())).assertIsDisplayed()
        composeTestRule.onNode(hasTestTag(Card.Clubs7.toString())).assertIsDisplayed()
        composeTestRule.onNode(hasTestTag(Card.Clubs8.toString())).assertIsDisplayed()
        composeTestRule.onNode(hasTestTag(Card.Clubs9.toString())).assertIsDisplayed()
        composeTestRule.onNode(hasTestTag(Card.Clubs10.toString())).assertIsDisplayed()
        composeTestRule.onNode(hasTestTag(Card.ClubsJack.toString())).assertIsDisplayed()

        assertThat(anyCardClicked).isFalse
    }

    @Test
    fun cardsNotSelectableCannotBeClicked() {
        launchTest()

        composeTestRule.onNode(hasTestTag(Card.Clubs3.toString())).assertIsDisplayed().performClick()
        composeTestRule.onNode(hasTestTag(Card.Clubs4.toString())).assertIsDisplayed().performClick()
        composeTestRule.onNode(hasTestTag(Card.Clubs5.toString())).assertIsDisplayed().performClick()
        composeTestRule.onNode(hasTestTag(Card.Clubs6.toString())).assertIsDisplayed().performClick()
        composeTestRule.onNode(hasTestTag(Card.Clubs7.toString())).assertIsDisplayed().performClick()

        assertThat(anyCardClicked).isFalse
    }

    @Test
    fun cardsSelectableCanBeClicked() {
        launchTest()

        anyCardClicked = false
        composeTestRule.onNode(hasTestTag(Card.Clubs2.toString())).assertIsDisplayed().performClick()
        assertThat(anyCardClicked).isTrue

        anyCardClicked = false
        composeTestRule.onNode(hasTestTag(Card.Clubs8.toString())).assertIsDisplayed().performClick()
        assertThat(anyCardClicked).isTrue

        anyCardClicked = false
        composeTestRule.onNode(hasTestTag(Card.Clubs9.toString())).assertIsDisplayed().performClick()
        assertThat(anyCardClicked).isTrue

        anyCardClicked = false
        composeTestRule.onNode(hasTestTag(Card.Clubs10.toString())).assertIsDisplayed().performClick()
        assertThat(anyCardClicked).isTrue

        anyCardClicked = false
        composeTestRule.onNode(hasTestTag(Card.ClubsJack.toString())).assertIsDisplayed().performClick()
        assertThat(anyCardClicked).isTrue
    }

    @Test
    fun cannotPickACardOrPassWhenControlsAreDisabled() {
        dashboardInfo = dashboardInfo.copy(
            localPlayerDashboard = dashboardInfo.localPlayerDashboard.copy(canPickACard = false, canPass = false)
        )

        launchTest()

        composeTestRule.onNode(hasTestTag("pickACardControl")).assertIsDisplayed().performClick()
        composeTestRule.onNode(hasTestTag("passControl")).assertIsDisplayed().performClick()

        assertThat(pickCardClicked).isFalse
        assertThat(passClicked).isFalse
    }

    private fun launchTest() {
        launchTestWithContent {
            DashboardScreen(
                dashboardInfo,
                onCardClick = { anyCardClicked = true },
                onPickClick = { pickCardClicked = true },
                onPassClick = { passClicked = true }
            )
        }
    }
}