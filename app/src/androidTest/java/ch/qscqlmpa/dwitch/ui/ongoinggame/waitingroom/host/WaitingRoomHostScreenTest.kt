package ch.qscqlmpa.dwitch.ui.ongoinggame.waitingroom.host

import androidx.compose.ui.test.*
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.base.BaseUiUnitTest
import ch.qscqlmpa.dwitch.ui.common.UiTags
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.host.HostCommunicationState
import ch.qscqlmpa.dwitchgame.ongoinggame.waitingroom.PlayerWrUi
import org.junit.Before
import org.junit.Test

class WaitingRoomHostScreenTest : BaseUiUnitTest() {

    private lateinit var players: List<PlayerWrUi>
    private var launchGameEnabled = false
    private lateinit var connectionState: HostCommunicationState

    @Before
    fun setup() {
        players = listOf(
            PlayerWrUi(10L, name = "Aragorn", connected = true, ready = true),
            PlayerWrUi(11L, name = "Legolas", connected = true, ready = false, kickable = true),
            PlayerWrUi(12L, name = "Gimli", connected = false, ready = false, kickable = true)
        )
        connectionState = HostCommunicationState.Open
    }

    @Test
    fun launchGameControlIsEnabled() {
        launchGameEnabled = true

        launchTest()

        composeTestRule.onNodeWithText(getString(R.string.launch_game))
            .assertIsEnabled()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(UiTags.toolbarNavigationIcon).assertIsDisplayed()
    }

    @Test
    fun launchGameControlIsDisabled() {
        launchGameEnabled = false

        launchTest()

        composeTestRule.onNodeWithText(getString(R.string.launch_game))
            .assertIsNotEnabled()
            .assertIsDisplayed()

        composeTestRule.onNodeWithTag(UiTags.toolbarNavigationIcon).assertIsDisplayed()
    }

    private fun launchTest() {
        launchTestWithContent {
            WaitingRoomHostBody(
                toolbarTitle = "Dwiitch",
                showAddComputerPlayer = true,
                players,
                launchGameEnabled,
                connectionState,
                onAddComputerPlayer = {},
                onLaunchGameClick = {},
                onCancelGameClick = {},
                onReconnectClick = {}
            )
        }
    }
}
