package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.guest

import androidx.compose.ui.test.*
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.base.BaseUiUnitTest
import ch.qscqlmpa.dwitch.ui.common.UiTags
import ch.qscqlmpa.dwitch.ui.model.UiCheckboxModel
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.guest.GuestCommunicationState
import ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom.PlayerWrUi
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import org.junit.Before
import org.junit.Test

class WaitingRoomGuestScreenTest : BaseUiUnitTest() {

    private lateinit var players: List<PlayerWrUi>
    private lateinit var ready: UiCheckboxModel
    private lateinit var connectionStatus: GuestCommunicationState

    @Before
    fun setup() {
        players = listOf(
            PlayerWrUi(10L, name = "Aragorn", PlayerConnectionState.CONNECTED, ready = true),
            PlayerWrUi(11L, name = "Legolas", PlayerConnectionState.CONNECTED, ready = false, kickable = false),
            PlayerWrUi(12L, name = "Gimli", PlayerConnectionState.DISCONNECTED, ready = false, kickable = false)
        )
        ready = UiCheckboxModel(enabled = true, checked = false)
        connectionStatus = GuestCommunicationState.Connected
    }

    @Test
    fun localPlayerIsReady() {
        ready = UiCheckboxModel(enabled = true, checked = true)
        connectionStatus = GuestCommunicationState.Connected

        launchTest()

        composeTestRule.onNodeWithTag(UiTags.localPlayerReadyControl, useUnmergedTree = true)
            .assertIsEnabled()
            .assertIsDisplayed()
            .assertIsOn()

        composeTestRule.onNodeWithTag(UiTags.localPlayerReadyText, useUnmergedTree = true)
            .assertTextContains(getString(R.string.ready))
            .assertIsDisplayed()
    }

    @Test
    fun localPlayerIsNotReady() {
        ready = UiCheckboxModel(enabled = true, checked = false)
        connectionStatus = GuestCommunicationState.Connected

        launchTest()

        composeTestRule.onNodeWithTag(UiTags.localPlayerReadyControl, useUnmergedTree = true)
            .assertIsEnabled()
            .assertIsDisplayed()
            .assertIsOff()

        composeTestRule.onNodeWithTag(UiTags.localPlayerReadyText, useUnmergedTree = true)
            .assertTextContains(getString(R.string.not_ready))
            .assertIsDisplayed()
    }

    private fun launchTest() {
        launchTestWithContent {
            WaitingRoomGuestScreen(
                toolbarTitle = "Dwiitch",
                players = players,
                ready = ready,
                connectionStatus = connectionStatus,
                onReadyClick = {},
                onLeaveClick = {},
                onReconnectClick = {}
            )
        }
    }
}
