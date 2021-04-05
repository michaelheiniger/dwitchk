package ch.qscqlmpa.dwitch.uitests

import androidx.compose.ui.test.*
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.assertTextIsDisplayedOnce
import ch.qscqlmpa.dwitch.uitests.base.BaseUiTest
import org.junit.Test

class NewGameAsHostTest : BaseUiTest() {

    @Test
    fun hostMustProvideAPlayerNameAndAGameNameToCreateAGame() {
        testRule.onNodeWithText(getString(R.string.create_game)).performClick()
        testRule.assertTextIsDisplayedOnce(getString(R.string.host_game))

        testRule.onNodeWithTag("playerName").assertTextEquals("")
        testRule.onNodeWithTag("gameName").assertTextEquals("")
        testRule.onNodeWithText(getString(R.string.host_game)).assertIsNotEnabled()

        testRule.onNodeWithTag("playerName").performTextInput("Mirlick")
        testRule.onNodeWithTag("gameName").performTextInput("Dwiiitch !")
        testRule.onNodeWithText(getString(R.string.host_game)).assertIsEnabled()
    }

    @Test
    fun hostCanAbortAndGoBackToHomeScreen() {
        testRule.onNodeWithText(getString(R.string.create_game)).performClick()
        testRule.assertTextIsDisplayedOnce(getString(R.string.host_game))

        testRule.onNodeWithText(getString(R.string.back_to_home_screen)).performClick()
        assertCurrentScreenIsHomeSreen()
    }
}
