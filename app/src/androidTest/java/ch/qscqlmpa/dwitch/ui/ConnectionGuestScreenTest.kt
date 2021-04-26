package ch.qscqlmpa.dwitch.ui

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.assertIsEnabled
import androidx.compose.ui.test.assertIsNotEnabled
import androidx.compose.ui.test.hasText
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.assertTextIsDisplayedOnce
import ch.qscqlmpa.dwitch.base.BaseUiUnitTest
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicationState
import org.junit.Test

class ConnectionGuestScreenTest : BaseUiUnitTest() {

    private lateinit var state: GuestCommunicationState

    @Test
    fun stateIsDisconnected() {
        state = GuestCommunicationState.Disconnected
        launchTest()

        composeTestRule.assertTextIsDisplayedOnce(getString(R.string.disconnected_from_host))
        composeTestRule.onNode(hasText(getString(R.string.reconnect)))
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    @Test
    fun stateIsConnecting() {
        state = GuestCommunicationState.Connecting
        launchTest()

        composeTestRule.assertTextIsDisplayedOnce(getString(R.string.guest_connecting))
        composeTestRule.onNode(hasText(getString(R.string.reconnect)))
            .assertIsDisplayed()
            .assertIsNotEnabled()
    }

    @Test
    fun stateIsConnected() {
        state = GuestCommunicationState.Connected
        launchTest()

        composeTestRule.assertTextIsDisplayedOnce(getString(R.string.connected_to_host))
        composeTestRule.onNode(hasText(getString(R.string.reconnect))).assertDoesNotExist()
    }

    @Test
    fun stateIsError() {
        state = GuestCommunicationState.Error
        launchTest()

        composeTestRule.assertTextIsDisplayedOnce(getString(R.string.guest_connection_error))
        composeTestRule.onNode(hasText(getString(R.string.reconnect)))
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    private fun launchTest() {
        launchTestWithContent {
            ConnectionGuestScreen(state, onReconnectClick = {}, onAbortClick = {})
        }
    }
}
