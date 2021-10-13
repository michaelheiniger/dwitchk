package ch.qscqlmpa.dwitch.ui.common

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.onNodeWithText
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.assertTextIsDisplayedOnce
import ch.qscqlmpa.dwitch.base.BaseUiUnitTest
import ch.qscqlmpa.dwitchgame.ingame.communication.host.HostCommunicationState
import org.junit.Test

class ConnectionHostScreenTest : BaseUiUnitTest() {

    private lateinit var state: HostCommunicationState

    @Test
    fun stateIsDisconnected() {
        state = HostCommunicationState.OfflineDisconnected(connectedToWlan = true)
        launchTest()

        composeTestRule.assertTextIsDisplayedOnce(getString(R.string.not_listening_for_guests))
        composeTestRule.onNodeWithText(getString(R.string.reconnect))
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    @Test
    fun stateIsOpening() {
        state = HostCommunicationState.Starting
        launchTest()

        composeTestRule.assertTextIsDisplayedOnce(getString(R.string.host_connecting))
        composeTestRule.onNodeWithText(getString(R.string.reconnect))
            .assertIsDisplayed()
            .assertIsNotEnabled()
    }

    @Test
    fun stateIsOpen() {
        state = HostCommunicationState.Online
        launchTest()

        composeTestRule.onNodeWithText(getString(R.string.reconnect)).assertDoesNotExist()
    }

    @Test
    fun stateIsError() {
        state = HostCommunicationState.OfflineFailed(connectedToWlan = true)
        launchTest()

        composeTestRule.assertTextIsDisplayedOnce(getString(R.string.host_connection_error))
        composeTestRule.onNodeWithText(getString(R.string.reconnect))
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    private fun launchTest() {
        launchTestWithContent {
            ConnectionHostScreen(state, onReconnectClick = {}, onAbortClick = {})
        }
    }
}
