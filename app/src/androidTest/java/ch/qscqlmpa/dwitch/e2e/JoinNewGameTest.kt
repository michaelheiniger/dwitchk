package ch.qscqlmpa.dwitch.e2e

import androidx.compose.ui.test.*
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.e2e.base.BaseUiTest
import ch.qscqlmpa.dwitch.ui.common.UiTags
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import org.junit.Test

class JoinNewGameTest : BaseUiTest() {

    @Test
    fun guestMustProvideANameToJoinTheGame() {
        advertiseGame1()
        advertiseGame2()

        testRule.onNodeWithText("Les Bronzés", substring = true).performClick()

        testRule.onNodeWithTag(UiTags.playerName).performTextClearance()
        testRule.onNodeWithTag(UiTags.playerName).assertTextEquals("")
        testRule.onNodeWithText(getString(R.string.join_game)).assertIsNotEnabled()

        testRule.onNodeWithTag(UiTags.playerName).performTextInput("Mébène")
        testRule.onNodeWithText(getString(R.string.join_game)).assertIsEnabled()
    }

    @Test
    fun guestCanAbortAndComeBackToHomeScreen() {
        advertiseGame1()
        advertiseGame2()

        testRule.onNodeWithText("Les Bronzés", substring = true)
            .assertIsDisplayed()
            .performClick()

        testRule.onNodeWithTag(UiTags.toolbarNavigationIcon)
            .assertIsDisplayed()
            .performClick()
        assertCurrentScreenIsHomeSreen()
    }

    private fun advertiseGame1() {
        advertiseGame(
            isNew = true,
            gameName = "Kaamelott",
            gameCommonId = GameCommonId(23),
            gamePort = 8890,
            senderIpAddress = "192.168.1.1",
            senderPort = 2454
        )
    }

    private fun advertiseGame2() {
        advertiseGame(
            isNew = true,
            gameName = "Les Bronzés",
            gameCommonId = GameCommonId(65),
            gamePort = 8891,
            senderIpAddress = "192.168.1.2",
            senderPort = 6543
        )
    }
}