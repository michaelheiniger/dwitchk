package ch.qscqlmpa.dwitch.e2e

import androidx.compose.ui.test.*
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.assertTextIsDisplayedOnce
import ch.qscqlmpa.dwitch.e2e.base.BaseUiTest
import ch.qscqlmpa.dwitch.ui.common.UiTags
import org.junit.Test

class NewGameAsHostTest : BaseUiTest() {

    @Test
    fun hostMustProvideAPlayerNameAndAGameNameToCreateAGame() {
        testRule.onNodeWithText(getString(R.string.create_game)).performClick()
        testRule.assertTextIsDisplayedOnce(getString(R.string.host_game))

        testRule.onNodeWithTag(UiTags.playerName).performTextClearance()
        testRule.onNodeWithTag(UiTags.gameName).performTextClearance()
        testRule.onNodeWithTag(UiTags.playerName).assertTextEquals("")
        testRule.onNodeWithTag(UiTags.gameName).assertTextEquals("")
        testRule.onNodeWithText(getString(R.string.host_game)).assertIsNotEnabled()

        testRule.onNodeWithTag(UiTags.playerName).performTextInput("Mirlick")
        testRule.onNodeWithTag(UiTags.gameName).performTextInput("Dwiiitch !")
        testRule.onNodeWithText(getString(R.string.host_game)).assertIsEnabled()
    }

    @Test
    fun hostCanAbortAndGoBackToHomeScreen() {
        testRule.onNodeWithText(getString(R.string.create_game)).performClick()
        testRule.assertTextIsDisplayedOnce(getString(R.string.host_game))

        testRule.onNodeWithTag(UiTags.toolbarNavigationIcon)
            .assertIsDisplayed()
            .performClick()
        assertCurrentScreenIsHomeSreen()
    }
}
