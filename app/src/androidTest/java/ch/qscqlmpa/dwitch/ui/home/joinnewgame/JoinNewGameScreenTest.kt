package ch.qscqlmpa.dwitch.ui.home.joinnewgame

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onNodeWithText
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.assertTextIsDisplayedOnce
import ch.qscqlmpa.dwitch.base.BaseUiUnitTest
import org.junit.Test

class JoinNewGameScreenTest : BaseUiUnitTest() {

    private lateinit var playerName: String
    private var joinGameControlEnabled = false

    @Test
    fun noInfoProvided() {
        playerName = ""
        joinGameControlEnabled = false

        launchTest()

        composeTestRule.onNodeWithText(getString(R.string.join_game)).assertIsNotEnabled()
        composeTestRule.assertTextIsDisplayedOnce(getString(R.string.back_to_home_screen))
    }

    @Test
    fun gameReadyToBeJoined() {
        playerName = "Legolas"
        joinGameControlEnabled = true

        launchTest()

        composeTestRule.assertTextIsDisplayedOnce(playerName)
        composeTestRule.onNodeWithText(getString(R.string.join_game)).assertIsEnabled()
        composeTestRule.assertTextIsDisplayedOnce(getString(R.string.back_to_home_screen))
    }

    private fun launchTest() {
        launchTestWithContent {
            JoinNewGameScreen(
                playerName = playerName,
                joinGameControlEnabled = joinGameControlEnabled,
                onPlayerNameChange = {},
                onJoinGameClick = {},
                onBackClick = {}
            )
        }
    }
}
