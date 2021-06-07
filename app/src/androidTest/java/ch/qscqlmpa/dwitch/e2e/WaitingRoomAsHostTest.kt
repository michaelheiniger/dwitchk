package ch.qscqlmpa.dwitch.e2e

import androidx.compose.ui.test.*
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.clickOnDialogConfirmButton
import ch.qscqlmpa.dwitch.e2e.base.BaseHostTest
import ch.qscqlmpa.dwitch.e2e.utils.WaitingRoomUtil.PLAYER_CONNECTED
import ch.qscqlmpa.dwitch.e2e.utils.WaitingRoomUtil.PLAYER_DISCONNECTED
import ch.qscqlmpa.dwitch.e2e.utils.WaitingRoomUtil.assertPlayerInWr
import ch.qscqlmpa.dwitch.ui.common.UiTags
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.websocket.server.test.PlayerHostTest
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.GuestMessageFactory
import org.assertj.core.api.Assertions.assertThat
import org.junit.Test

class WaitingRoomAsHostTest : BaseHostTest() {

    @Test
    fun goToWaitingRoomScreen() {
        goToWaitingRoom()

        testRule.assertPlayerInWr(hostName, ready = false, connectionState = getString(PLAYER_CONNECTED))

        testRule.onNodeWithText(getString(R.string.launch_game))
            .assertIsDisplayed()
            .assertIsNotEnabled()
    }

    @Test
    fun guest1JoinsWaitingRoom() {
        goToWaitingRoom()

        guestJoinsGame(PlayerHostTest.Guest1)

        testRule.assertPlayerInWr(hostName)
        testRule.assertPlayerInWr(PlayerHostTest.Guest1.name, ready = false, connectionState = getString(PLAYER_CONNECTED))
    }

    @Test
    fun someGuestBecomesReady() {
        goToWaitingRoom()

        guestJoinsGame(PlayerHostTest.Guest1)

        testRule.assertPlayerInWr(hostName, ready = true)
        testRule.assertPlayerInWr(PlayerHostTest.Guest1.name, ready = false)

        guestBecomesReady(PlayerHostTest.Guest1)

        testRule.assertPlayerInWr(hostName, ready = true)
        testRule.assertPlayerInWr(PlayerHostTest.Guest1.name, ready = true)
    }

    @Test
    fun guest1LeavesGame() {
        goToWaitingRoom()

        guestJoinsGame(PlayerHostTest.Guest1)
        guestJoinsGame(PlayerHostTest.Guest2)

        testRule.assertPlayerInWr(hostName, ready = true)
        testRule.assertPlayerInWr(PlayerHostTest.Guest1.name)
        testRule.assertPlayerInWr(PlayerHostTest.Guest2.name)

        guestLeavesGame(PlayerHostTest.Guest1)

        testRule.assertPlayerInWr(hostName, ready = true)
        testRule.assertPlayerInWr(PlayerHostTest.Guest2.name)
    }

    @Test
    fun hostKicksGuest1OffGame() {
        goToWaitingRoom()

        guestJoinsGame(PlayerHostTest.Guest1)
        guestJoinsGame(PlayerHostTest.Guest2)

        testRule.assertPlayerInWr(hostName, ready = true)
        testRule.assertPlayerInWr(PlayerHostTest.Guest1.name)
        testRule.assertPlayerInWr(PlayerHostTest.Guest2.name)

        testRule.onNodeWithTag("${UiTags.kickPlayer}-${PlayerHostTest.Guest1.name}").performClick()
        waitForNextNMessageSentByHost(2) // KickPlayerMessage and WaitingRoomStateUpdatedMessage

        testRule.assertPlayerInWr(hostName, ready = true)
        testRule.assertPlayerInWr(PlayerHostTest.Guest2.name)
    }

    @Test
    fun guest1DisconnectsAndComesBackWaitingRoom() {
        goToWaitingRoom()

        guestJoinsGame(PlayerHostTest.Guest1)
        guestJoinsGame(PlayerHostTest.Guest2)

        testRule.assertPlayerInWr(hostName, getString(PLAYER_CONNECTED))
        testRule.assertPlayerInWr(PlayerHostTest.Guest1.name, getString(PLAYER_CONNECTED))
        testRule.assertPlayerInWr(PlayerHostTest.Guest2.name, getString(PLAYER_CONNECTED))

        guestDisconnects(PlayerHostTest.Guest1)

        testRule.assertPlayerInWr(hostName, getString(PLAYER_CONNECTED))
        testRule.assertPlayerInWr(PlayerHostTest.Guest1.name, getString(PLAYER_DISCONNECTED))
        testRule.assertPlayerInWr(PlayerHostTest.Guest2.name, getString(PLAYER_CONNECTED))

        guestRejoinsGame(PlayerHostTest.Guest1)

        testRule.assertPlayerInWr(hostName, getString(PLAYER_CONNECTED))
        testRule.assertPlayerInWr(PlayerHostTest.Guest1.name, getString(PLAYER_CONNECTED))
        testRule.assertPlayerInWr(PlayerHostTest.Guest2.name, getString(PLAYER_CONNECTED))
    }

    @Test
    fun hostCancelsGame() {
        goToWaitingRoom()

        guestJoinsGame(PlayerHostTest.Guest1)

        testRule.onNodeWithTag(UiTags.toolbarNavigationIcon)
            .assertIsDisplayed()
            .performClick()
        testRule.clickOnDialogConfirmButton()

        incrementGameIdlingResource("Communication server stopped.")

        val messageSent = waitForNextMessageSentByHost()
        assertThat(messageSent).isInstanceOf(Message.CancelGameMessage::class.java)

        assertCurrentScreenIsHomeSreen()
    }

    private fun guestRejoinsGame(guest: PlayerHostTest) {
        val player = getGuest(guest)
        incrementGameIdlingResource("Guest rejoins game ($guest)")
        serverTestStub.connectClientToServer(guest)
        val gameCommonId = inGameStore.getGame().gameCommonId
        val rejoinMessage = GuestMessageFactory.createRejoinGameMessage(gameCommonId, player.dwitchId)
        serverTestStub.clientSendsMessageToServer(guest, rejoinMessage)

        val messageSent1 = waitForNextMessageSentByHost()
        assertThat(messageSent1).isInstanceOf(Message.RejoinGameAckMessage::class.java)

        val messageSent2 = waitForNextMessageSentByHost()
        assertThat(messageSent2).isInstanceOf(Message.WaitingRoomStateUpdateMessage::class.java)
    }
}


