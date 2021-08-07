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
        goToWaitingRoomWithHostAndAllGuests()

        testRule.assertPlayerInWr(PlayerGuestTest.Host.info.name, getString(PLAYER_CONNECTED))
        testRule.assertPlayerInWr(PlayerGuestTest.LocalGuest.info.name, getString(PLAYER_CONNECTED))
        testRule.assertPlayerInWr(PlayerGuestTest.Guest2.info.name, getString(PLAYER_CONNECTED))
        testRule.assertPlayerInWr(PlayerGuestTest.Guest3.info.name, getString(PLAYER_CONNECTED))
    }

    @Test
    fun playerBecomesReady() {
        goToWaitingRoomWithHostAndAllGuests()

        testRule.assertPlayerInWr(PlayerGuestTest.LocalGuest.info.name, ready = false)

        localPlayerToggleReadyCheckbox()
        testRule.assertCheckboxChecked(UiTags.localPlayerReadyControl, checked = true)
        testRule.assertPlayerInWr(PlayerGuestTest.LocalGuest.info.name, ready = true)

        localPlayerToggleReadyCheckbox()
        testRule.assertCheckboxChecked(UiTags.localPlayerReadyControl, checked = false)
        testRule.assertPlayerInWr(PlayerGuestTest.LocalGuest.info.name, ready = false)
    }

    @Test
    fun playerLeavesGame() {
        goToWaitingRoomWithHostAndAllGuests()

        testRule.assertPlayerInWr(PlayerGuestTest.LocalGuest.info.name, ready = false)

        testRule.onNodeWithTag(UiTags.toolbarNavigationIcon).performClick()
        testRule.clickOnDialogConfirmButton()

        assertCurrentScreenIsHomeSreen()
    }

    @Test
    fun localPlayerGetsDisconnected() {
        goToWaitingRoomWithHostAndAllGuests()

        testRule.assertPlayerInWr(PlayerGuestTest.Host.info.name, getString(PLAYER_CONNECTED))
        testRule.assertPlayerInWr(PlayerGuestTest.LocalGuest.info.name, getString(PLAYER_CONNECTED))
        testRule.assertPlayerInWr(PlayerGuestTest.Guest2.info.name, getString(PLAYER_CONNECTED))
        testRule.assertPlayerInWr(PlayerGuestTest.Guest3.info.name, getString(PLAYER_CONNECTED))

        clientTestStub.breakConnectionWithServer()
        incrementGameIdlingResource("Connection with host broken: Communication state will be updated")
        incrementGameIdlingResource("Local player is disconnected so new WR players state")

        testRule.assertPlayerInWr(PlayerGuestTest.Host.info.name, getString(PLAYER_DISCONNECTED))
        testRule.assertPlayerInWr(PlayerGuestTest.LocalGuest.info.name, getString(PLAYER_DISCONNECTED))
        testRule.assertPlayerInWr(PlayerGuestTest.Guest2.info.name, getString(PLAYER_DISCONNECTED))
        testRule.assertPlayerInWr(PlayerGuestTest.Guest3.info.name, getString(PLAYER_DISCONNECTED))

        testRule.onNodeWithText(getString(R.string.disconnected_from_host))
        testRule.onNodeWithTag(UiTags.reconnect)
            .assertIsEnabled()
            .assertIsDisplayed()
    }

    @Test
    fun gameCanceled() {
        goToWaitingRoomWithHostAndAllGuests()

        clientTestStub.serverSendsMessageToClient(Message.CancelGameMessage)
        clientTestStub.serverClosesConnectionWithClient()
        incrementGameIdlingResource("Game canceled: WR players state will be updated")
        incrementGameIdlingResource("Game canceled: Communication state will be updated")

        testRule.clickOnDialogConfirmButton()

        assertCurrentScreenIsHomeSreen()
    }

    @Test
    fun playerKickedOffGame() {
        goToWaitingRoomWithHostAndAllGuests()

        clientTestStub.serverSendsMessageToClient(Message.KickPlayerMessage(PlayerGuestTest.LocalGuest.info.dwitchId))
        clientTestStub.serverClosesConnectionWithClient()
        incrementGameIdlingResource("Local player kicked off: WR players state will be updated")
        incrementGameIdlingResource("Local player kicked off: Communication state will be updated")

        testRule.onNodeWithText(getString(R.string.ok)).performClick()

        assertCurrentScreenIsHomeSreen()
    }
}
