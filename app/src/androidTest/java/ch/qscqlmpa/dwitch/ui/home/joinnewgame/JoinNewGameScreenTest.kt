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
    }

    @Test
    fun gameReadyToBeJoined() {
        playerName = "Legolas"
        joinGameControlEnabled = true

        launchTest()

        composeTestRule.assertTextIsDisplayedOnce(playerName)
        composeTestRule.onNodeWithText(getString(R.string.join_game)).assertIsEnabled()
    }

    private fun launchTest() {
        launchTestWithContent {
            JoinNewGameBody(
                notification = JoinNewGameNotification.None,
                gameName = "Dwiiitch",
                playerName = playerName,
                joinGameControlEnabled = joinGameControlEnabled,
                loading = false,
                onPlayerNameChange = {},
                onJoinGameClick = {},
                onBackClick = {},
            )
        }
    }
}
