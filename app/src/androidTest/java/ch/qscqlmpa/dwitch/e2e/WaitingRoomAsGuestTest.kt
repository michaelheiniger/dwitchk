package ch.qscqlmpa.dwitch.e2e

import androidx.compose.ui.test.*
import ch.qscqlmpa.dwitch.PlayerGuestTest
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.assertCheckboxChecked
import ch.qscqlmpa.dwitch.clickOnDialogConfirmButton
import ch.qscqlmpa.dwitch.e2e.base.BaseGuestTest
import ch.qscqlmpa.dwitch.e2e.utils.WaitingRoomUtil.PLAYER_CONNECTED
import ch.qscqlmpa.dwitch.e2e.utils.WaitingRoomUtil.PLAYER_DISCONNECTED
import ch.qscqlmpa.dwitch.e2e.utils.WaitingRoomUtil.assertPlayerInWr
import ch.qscqlmpa.dwitch.ui.common.UiTags
import ch.qscqlmpa.dwitchcommunication.model.Message
import org.junit.Test

class WaitingRoomAsGuestTest : BaseGuestTest() {

    @Test
    fun goToWaitingRoomScreen() {
        goToWaitingRoom()

        testRule.assertPlayerInWr(PlayerGuestTest.Host.name, getString(PLAYER_CONNECTED))
        testRule.assertPlayerInWr(PlayerGuestTest.LocalGuest.name, getString(PLAYER_CONNECTED))
        testRule.assertPlayerInWr(PlayerGuestTest.Guest2.name, getString(PLAYER_CONNECTED))
        testRule.assertPlayerInWr(PlayerGuestTest.Guest3.name, getString(PLAYER_CONNECTED))
    }

    @Test
    fun playerBecomesReady() {
        goToWaitingRoom()

        testRule.assertPlayerInWr(PlayerGuestTest.LocalGuest.name, ready = false)

        localPlayerToggleReadyCheckbox()
        testRule.assertCheckboxChecked(UiTags.localPlayerReadyControl, checked = true)
        testRule.assertPlayerInWr(PlayerGuestTest.LocalGuest.name, ready = true)

        localPlayerToggleReadyCheckbox()
        testRule.assertCheckboxChecked(UiTags.localPlayerReadyControl, checked = false)
        testRule.assertPlayerInWr(PlayerGuestTest.LocalGuest.name, ready = false)
    }

    @Test
    fun playerLeavesGame() {
        goToWaitingRoom()

        testRule.assertPlayerInWr(PlayerGuestTest.LocalGuest.name, ready = false)

        testRule.onNodeWithTag(UiTags.toolbarNavigationIcon).performClick()
        testRule.clickOnDialogConfirmButton()

        assertCurrentScreenIsHomeSreen()
    }

    @Test
    fun localPlayerGetsDisconnected() {
        goToWaitingRoom()

        testRule.assertPlayerInWr(PlayerGuestTest.Host.name, getString(PLAYER_CONNECTED))
        testRule.assertPlayerInWr(PlayerGuestTest.LocalGuest.name, getString(PLAYER_CONNECTED))
        testRule.assertPlayerInWr(PlayerGuestTest.Guest2.name, getString(PLAYER_CONNECTED))
        testRule.assertPlayerInWr(PlayerGuestTest.Guest3.name, getString(PLAYER_CONNECTED))

        clientTestStub.breakConnectionWithHost()

        testRule.assertPlayerInWr(PlayerGuestTest.Host.name, getString(PLAYER_DISCONNECTED))
        testRule.assertPlayerInWr(PlayerGuestTest.LocalGuest.name, getString(PLAYER_DISCONNECTED))
        testRule.assertPlayerInWr(PlayerGuestTest.Guest2.name, getString(PLAYER_DISCONNECTED))
        testRule.assertPlayerInWr(PlayerGuestTest.Guest3.name, getString(PLAYER_DISCONNECTED))

        testRule.onNodeWithText(getString(R.string.disconnected_from_host))
        testRule.onNodeWithTag(UiTags.reconnect)
            .assertIsEnabled()
            .assertIsDisplayed()
    }

    @Test
    fun gameCanceled() {
        goToWaitingRoom()

        clientTestStub.serverSendsMessageToClient(Message.CancelGameMessage)

        testRule.clickOnDialogConfirmButton()

        assertCurrentScreenIsHomeSreen()
    }

    @Test
    fun playerKickedOffGame() {
        goToWaitingRoom()

        clientTestStub.serverSendsMessageToClient(Message.KickPlayerMessage(PlayerGuestTest.LocalGuest.id))

        testRule.onNodeWithText(getString(R.string.ok)).performClick()

        assertCurrentScreenIsHomeSreen()
    }
}
