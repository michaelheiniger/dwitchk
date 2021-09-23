package ch.qscqlmpa.dwitch.e2e

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.e2e.base.BaseE2eTest
import ch.qscqlmpa.dwitch.ui.common.UiTags
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import org.junit.Test
import java.util.*

class JoinNewGameTest : BaseE2eTest() {

    @Test
    fun guestMustProvideANameToJoinTheGame() {
        advertiseGame1()
        advertiseGame2()

        testRule.onNodeWithText("Les Bronzés", substring = true)
            .assertIsDisplayed()
            .performClick()

        testRule.playerName().performTextClearance()
        testRule.playerName().assertTextEquals("")
        testRule.joinGameButton().assertIsNotEnabled()

        testRule.playerName().performTextInput("Mébène")
        testRule.joinGameButton().assertIsEnabled()
    }

    @Test
    fun guestCanAbortAndComeBackToHomeScreen() {
        advertiseGame1()
        advertiseGame2()

        testRule.onNodeWithText("Les Bronzés", substring = true)
            .assertIsDisplayed()
            .performClick()

        testRule.joinGameButton().assertIsDisplayed()

        testRule.onNodeWithTag(UiTags.toolbarNavigationIcon)
            .assertIsDisplayed()
            .performClick()
        assertCurrentScreenIsHomeSreen()
    }

    private fun ComposeContentTestRule.playerName(): SemanticsNodeInteraction {
        return onNodeWithTag(UiTags.playerName, useUnmergedTree = true)
    }

    private fun ComposeContentTestRule.joinGameButton(): SemanticsNodeInteraction {
        return onNodeWithText(getString(R.string.join_game))
    }

    private fun advertiseGame1() {
        advertiseGame(
            isNew = true,
            gameName = "Kaamelott",
            gameCommonId = GameCommonId(UUID.randomUUID()),
            gamePort = 8890,
            gameIpAddress = "192.168.1.1",
            senderPort = 2454
        )
    }

    private fun advertiseGame2() {
        advertiseGame(
            isNew = true,
            gameName = "Les Bronzés",
            gameCommonId = GameCommonId(UUID.randomUUID()),
            gamePort = 8891,
            gameIpAddress = "192.168.1.2",
            senderPort = 6543
        )
    }
}
