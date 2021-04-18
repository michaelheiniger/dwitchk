package ch.qscqlmpa.dwitch.e2e

import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import ch.qscqlmpa.dwitch.PlayerGuestTest
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.assertCheckboxChecked
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
        testRule.assertCheckboxChecked(UiTags.localPlayerReadyCheckbox, checked = true)
        testRule.assertPlayerInWr(PlayerGuestTest.LocalGuest.name, ready = true)

        localPlayerToggleReadyCheckbox()
        testRule.assertCheckboxChecked(UiTags.localPlayerReadyCheckbox, checked = false)
        testRule.assertPlayerInWr(PlayerGuestTest.LocalGuest.name, ready = false)
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
    }

    @Test
    fun gameCanceled() {
        goToWaitingRoom()

        idlingResourceIncrement()
        clientTestStub.serverSendsMessageToClient(Message.CancelGameMessage)

        testRule.onNodeWithText(getString(R.string.ok)).performClick()

        assertCurrentScreenIsHomeSreen()
    }
}
