package ch.qscqlmpa.dwitch.ui.home.hostnewgame

import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onNodeWithText
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.assertTextIsDisplayedOnce
import ch.qscqlmpa.dwitch.base.BaseUiUnitTest
import org.junit.Test

class HostNewGameScreenTest : BaseUiUnitTest() {

    private lateinit var playerName: String
    private lateinit var gameName: String
    private var hostGameControlEnabled = false

    @Test
    fun noHostGameInfoProvided() {
        playerName = ""
        gameName = ""
        hostGameControlEnabled = false

        launchTest()

        composeTestRule.onNodeWithText(getString(R.string.host_game)).assertIsNotEnabled()
    }

    @Test
    fun gameReadyToBeHosted() {
        playerName = "Aragorn"
        gameName = "LOTR"
        hostGameControlEnabled = true

        launchTest()

        composeTestRule.assertTextIsDisplayedOnce("Aragorn")
        composeTestRule.assertTextIsDisplayedOnce("LOTR")
        composeTestRule.onNodeWithText(getString(R.string.host_game)).assertIsEnabled()
    }

    private fun launchTest() {
        launchTestWithContent {
            HostNewGameBody(
                playerName = playerName,
                gameName = gameName,
                hostGameControlEnabled = hostGameControlEnabled,
                loading = false,
                onPlayerNameChange = {},
                onGameNameChange = {},
                onCreateGameClick = {},
                onBackClick = {}
            )
        }
    }
}
