package ch.qscqlmpa.dwitch.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasText
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.assertTextIsDisplayedOnce
import ch.qscqlmpa.dwitch.base.BaseUiUnitTest
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicationState
import org.junit.Test

class ConnectionHostScreenTest : BaseUiUnitTest() {

    private lateinit var state: HostCommunicationState

    @Test
    fun stateIsDisconnected() {
        state = HostCommunicationState.Closed
        launchTest()

        composeTestRule.assertTextIsDisplayedOnce(getString(R.string.not_listening_for_guests))
        composeTestRule.onNode(hasText(getString(R.string.reconnect)))
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    @Test
    fun stateIsOpening() {
        state = HostCommunicationState.Opening
        launchTest()

        composeTestRule.assertTextIsDisplayedOnce(getString(R.string.host_connecting))
        composeTestRule.onNode(hasText(getString(R.string.reconnect)))
            .assertIsDisplayed()
            .assertIsNotEnabled()
    }

    @Test
    fun stateIsOpen() {
        state = HostCommunicationState.Open
        launchTest()

        composeTestRule.assertTextIsDisplayedOnce(getString(R.string.connected_to_host))
        composeTestRule.onNode(hasText(getString(R.string.reconnect))).assertDoesNotExist()
    }

    @Test
    fun stateIsError() {
        state = HostCommunicationState.Error
        launchTest()

        composeTestRule.assertTextIsDisplayedOnce(getString(R.string.host_connection_error))
        composeTestRule.onNode(hasText(getString(R.string.reconnect)))
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    private fun launchTest() {
        launchTestWithContent {
            ConnectionHostScreen(state, onReconnectClick = {}, onAbortClick = {})
        }
    }
}
