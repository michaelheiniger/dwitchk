package ch.qscqlmpa.dwitch.ui.common

import androidx.compose.ui.test.*
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.assertTextIsDisplayedOnce
import ch.qscqlmpa.dwitch.base.BaseUiUnitTest
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationState
import org.junit.Test

class ConnectionGuestScreenTest : BaseUiUnitTest() {

    private lateinit var state: GuestCommunicationState

    @Test
    fun stateIsDisconnected() {
        state = GuestCommunicationState.Disconnected(connectedToWlan = true)
        launchTest()

        composeTestRule.assertTextIsDisplayedOnce(getString(R.string.disconnected_from_host))
        composeTestRule.onNodeWithText(getString(R.string.reconnect))
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    @Test
    fun stateIsConnecting() {
        state = GuestCommunicationState.Connecting
        launchTest()

        composeTestRule.assertTextIsDisplayedOnce(getString(R.string.guest_connecting))
        composeTestRule.onNodeWithText(getString(R.string.reconnect))
            .assertIsDisplayed()
            .assertIsNotEnabled()
    }

    @Test
    fun stateIsConnected() {
        state = GuestCommunicationState.Connected
        launchTest()

        composeTestRule.onNodeWithTag(getString(R.string.reconnect)).assertDoesNotExist()
    }

    @Test
    fun stateIsError() {
        state = GuestCommunicationState.Error(connectedToWlan = true)
        launchTest()

        composeTestRule.assertTextIsDisplayedOnce(getString(R.string.guest_connection_error))
        composeTestRule.onNodeWithText(getString(R.string.reconnect))
            .assertIsDisplayed()
            .assertIsEnabled()
    }

    private fun launchTest() {
        launchTestWithContent {
            ConnectionGuestScreen(state, onReconnectClick = {}, onAbortClick = {})
        }
    }
}
