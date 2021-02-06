package ch.qscqlmpa.dwitch.uitests

import ch.qscqlmpa.dwitch.PlayerGuestTest
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.uitests.base.BaseGuestTest
import ch.qscqlmpa.dwitch.uitests.utils.UiUtil.assertCheckboxChecked
import ch.qscqlmpa.dwitch.uitests.utils.UiUtil.clickOnButton
import ch.qscqlmpa.dwitch.uitests.utils.UiUtil.elementIsDisplayed
import ch.qscqlmpa.dwitch.uitests.utils.WaitingRoomUtil.PLAYER_CONNECTED
import ch.qscqlmpa.dwitch.uitests.utils.WaitingRoomUtil.PLAYER_DISCONNECTED
import ch.qscqlmpa.dwitch.uitests.utils.WaitingRoomUtil.assertPlayerInWr
import ch.qscqlmpa.dwitchcommunication.model.Message
import org.junit.Test

class WaitingRoomAsGuestTest : BaseGuestTest() {

    @Test
    fun goToWaitingRoomScreen() {
        launch()

        goToWaitingRoom()

        assertPlayerInWr(0, PlayerGuestTest.Host.name, PLAYER_CONNECTED)
        assertPlayerInWr(1, PlayerGuestTest.LocalGuest.name, PLAYER_CONNECTED)
        assertPlayerInWr(2, PlayerGuestTest.Guest2.name, PLAYER_CONNECTED)
        assertPlayerInWr(3, PlayerGuestTest.Guest3.name, PLAYER_CONNECTED)
    }

    @Test
    fun playerBecomesReady() {
        launch()

        goToWaitingRoom()

        assertPlayerInWr(1, PlayerGuestTest.LocalGuest.name, ready = false)

        localPlayerToggleReadyCheckbox()
        assertCheckboxChecked(R.id.localPlayerReadyCkb, checked = true)
        assertPlayerInWr(1, PlayerGuestTest.LocalGuest.name, ready = true)

        localPlayerToggleReadyCheckbox()
        assertCheckboxChecked(R.id.localPlayerReadyCkb, checked = false)
        assertPlayerInWr(1, PlayerGuestTest.LocalGuest.name, ready = false)
    }

    @Test
    fun localPlayerGetsDisconnected() {
        launch()

        goToWaitingRoom()

        assertPlayerInWr(0, PlayerGuestTest.Host.name, PLAYER_CONNECTED)
        assertPlayerInWr(1, PlayerGuestTest.LocalGuest.name, PLAYER_CONNECTED)
        assertPlayerInWr(2, PlayerGuestTest.Guest2.name, PLAYER_CONNECTED)
        assertPlayerInWr(3, PlayerGuestTest.Guest3.name, PLAYER_CONNECTED)

        clientTestStub.breakConnectionWithHost()
        dudeWaitAMillisSec()

        assertPlayerInWr(0, PlayerGuestTest.Host.name, PLAYER_DISCONNECTED)
        assertPlayerInWr(1, PlayerGuestTest.LocalGuest.name, PLAYER_DISCONNECTED)
        assertPlayerInWr(2, PlayerGuestTest.Guest2.name, PLAYER_DISCONNECTED)
        assertPlayerInWr(3, PlayerGuestTest.Guest3.name, PLAYER_DISCONNECTED)
    }

    @Test
    fun gameCanceled() {
        launch()

        goToWaitingRoom()

        clientTestStub.serverSendsMessageToClient(Message.CancelGameMessage, false)
        dudeWaitAMillisSec()

        clickOnButton(R.id.btnOk)
        dudeWaitAMillisSec()

        elementIsDisplayed(R.id.gameListTv)
    }
}
