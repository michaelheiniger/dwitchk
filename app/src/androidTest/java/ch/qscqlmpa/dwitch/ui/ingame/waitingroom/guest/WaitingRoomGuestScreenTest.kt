package ch.qscqlmpa.dwitch.ui.ingame.waitingroom.guest

import androidx.compose.ui.test.*
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.base.BaseUiUnitTest
import ch.qscqlmpa.dwitch.ui.common.UiTags
import ch.qscqlmpa.dwitch.ui.model.UiCheckboxModel
import ch.qscqlmpa.dwitchgame.ingame.communication.guest.GuestCommunicationState
import ch.qscqlmpa.dwitchgame.ingame.waitingroom.PlayerWrUi
import org.junit.Before
import org.junit.Test

class WaitingRoomGuestScreenTest : BaseUiUnitTest() {

    private lateinit var players: List<PlayerWrUi>
    private lateinit var ready: UiCheckboxModel
    private lateinit var connectionStatus: GuestCommunicationState

    @Before
    fun setup() {
        players = listOf(
            PlayerWrUi(10L, name = "Aragorn", connected = true, ready = true),
            PlayerWrUi(11L, name = "Legolas", connected = true, ready = false, kickable = false),
            PlayerWrUi(12L, name = "Gimli", connected = false, ready = false, kickable = false)
        )
        ready = UiCheckboxModel(enabled = true, checked = false)
        connectionStatus = GuestCommunicationState.Connected
    }

    @Test
    fun localPlayerIsReady() {
        // Given
        ready = UiCheckboxModel(enabled = true, checked = true)
        connectionStatus = GuestCommunicationState.Connected

        // When
        launchTest()

        // Then
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
        // Given
        ready = UiCheckboxModel(enabled = true, checked = false)
        connectionStatus = GuestCommunicationState.Connected

        // When
        launchTest()

        // Then
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
            WaitingRoomGuestBody(
                toolbarTitle = "Dwiitch",
                players = players,
                ready = ready,
                notification = WaitingRoomGuestNotification.None,
                connectionState = GuestCommunicationState.Connected,
                onReadyClick = {},
                onLeaveConfirmClick = {},
                onReconnectClick = {},
                onGameCanceledAcknowledge = {},
                onKickOffGameAcknowledge = {}
            )
        }
    }
}
