package ch.qscqlmpa.dwitch.e2e

import androidx.compose.ui.test.*
import androidx.compose.ui.test.junit4.ComposeContentTestRule
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.e2e.base.BaseUiTest
import ch.qscqlmpa.dwitch.ui.common.UiTags
import org.junit.Test

class HostNewGameTest : BaseUiTest() {

    @Test
    fun hostMustProvideAPlayerNameAndAGameNameToCreateAGame() {
        testRule.createNewGameButton().performClick()
        testRule.hostGameButton().assertIsDisplayed()

        testRule.playerName().performTextClearance()
        testRule.gameName().performTextClearance()
        testRule.playerName().assertTextEquals("")
        testRule.gameName().assertTextEquals("")
        testRule.hostGameButton().assertIsNotEnabled()

        testRule.playerName().performTextInput("Mirlick")
        testRule.gameName().performTextInput("Dwiiitch !")
        testRule.hostGameButton().assertIsEnabled()
    }

    @Test
    fun hostCanAbortAndGoBackToHomeScreen() {
        testRule.createNewGameButton().performClick()
        testRule.hostGameButton().assertIsDisplayed()

        testRule.onNodeWithTag(UiTags.toolbarNavigationIcon)
            .assertIsDisplayed()
            .performClick()
        assertCurrentScreenIsHomeSreen()
    }

    private fun ComposeContentTestRule.createNewGameButton(): SemanticsNodeInteraction {
        return onNodeWithText(getString(R.string.create_new_game))
    }

    private fun ComposeContentTestRule.playerName(): SemanticsNodeInteraction {
        return onNodeWithTag(UiTags.playerName, useUnmergedTree = true)
    }

    private fun ComposeContentTestRule.gameName(): SemanticsNodeInteraction {
        return onNodeWithTag(UiTags.gameName, useUnmergedTree = true)
    }

    private fun ComposeContentTestRule.hostGameButton(): SemanticsNodeInteraction {
        return onNodeWithText(getString(R.string.host_game))
    }
}
