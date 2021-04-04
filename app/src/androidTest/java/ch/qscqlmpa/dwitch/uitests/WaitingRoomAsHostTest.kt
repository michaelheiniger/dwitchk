package ch.qscqlmpa.dwitch.uitests

import ch.qscqlmpa.dwitch.uitests.base.BaseHostTest
import org.junit.Ignore

@Ignore
class WaitingRoomAsHostTest : BaseHostTest() {

//    @Test
//    fun goToWaitingRoomScreen() {
//        launch()
//
//        goToWaitingRoom()
//
//        WaitingRoomUtil.assertPlayerInWr(0, hostName, ready = true, connectionState = PLAYER_CONNECTED)
//
//        UiUtil.assertControlEnabled(R.id.launchGameBtn, enabled = false)
//    }
//
//    @Test
//    fun guestJoinsWaitingRoom() {
//        launch()
//
//        goToWaitingRoom()
//
//        WaitingRoomUtil.assertPlayerInWr(0, hostName)
//
//        guestJoinsGame(PlayerHostTest.Guest1)
//
//        WaitingRoomUtil.assertPlayerInWr(0, hostName)
//        WaitingRoomUtil.assertPlayerInWr(1, PlayerHostTest.Guest1.name, ready = false, connectionState = PLAYER_CONNECTED)
//    }
//
//    @Test
//    fun guestBecomesReady() {
//        launch()
//
//        goToWaitingRoom()
//
//        guestJoinsGame(PlayerHostTest.Guest1)
//
//        // Players sorted according to their name ASC
//        WaitingRoomUtil.assertPlayerInWr(0, hostName, ready = true)
//        WaitingRoomUtil.assertPlayerInWr(1, PlayerHostTest.Guest1.name, ready = false)
//
//        guestBecomesReady(PlayerHostTest.Guest1)
//
//        WaitingRoomUtil.assertPlayerInWr(0, hostName, ready = true)
//        WaitingRoomUtil.assertPlayerInWr(1, PlayerHostTest.Guest1.name, ready = true)
//    }
//
//    @Test
//    fun guest1LeavesGame() {
//        launch()
//
//        goToWaitingRoom()
//
//        guestJoinsGame(PlayerHostTest.Guest1)
//        guestJoinsGame(PlayerHostTest.Guest2)
//
//        WaitingRoomUtil.assertPlayerInWr(0, hostName)
//        WaitingRoomUtil.assertPlayerInWr(1, PlayerHostTest.Guest1.name)
//        WaitingRoomUtil.assertPlayerInWr(2, PlayerHostTest.Guest2.name)
//
//        guestLeavesGame(PlayerHostTest.Guest1)
//
//        WaitingRoomUtil.assertPlayerInWr(0, hostName)
//        WaitingRoomUtil.assertPlayerInWr(1, PlayerHostTest.Guest2.name)
//    }
//
//    @Test
//    fun guest1DisconnectsAndComesBackWaitingRoom() {
//        launch()
//
//        goToWaitingRoom()
//
//        guestJoinsGame(PlayerHostTest.Guest1)
//        guestJoinsGame(PlayerHostTest.Guest2)
//
//        WaitingRoomUtil.assertPlayerInWr(0, hostName, PLAYER_CONNECTED)
//        WaitingRoomUtil.assertPlayerInWr(1, PlayerHostTest.Guest1.name, PLAYER_CONNECTED)
//        WaitingRoomUtil.assertPlayerInWr(2, PlayerHostTest.Guest2.name, PLAYER_CONNECTED)
//
//        guestDisconnects(PlayerHostTest.Guest1)
//
//        WaitingRoomUtil.assertPlayerInWr(0, hostName, PLAYER_CONNECTED)
//        WaitingRoomUtil.assertPlayerInWr(1, PlayerHostTest.Guest1.name, PLAYER_DISCONNECTED)
//        WaitingRoomUtil.assertPlayerInWr(2, PlayerHostTest.Guest2.name, PLAYER_CONNECTED)
//
//        guestRejoinsGame(PlayerHostTest.Guest1)
//
//        WaitingRoomUtil.assertPlayerInWr(0, hostName, PLAYER_CONNECTED)
//        WaitingRoomUtil.assertPlayerInWr(1, PlayerHostTest.Guest1.name, PLAYER_CONNECTED)
//        WaitingRoomUtil.assertPlayerInWr(2, PlayerHostTest.Guest2.name, PLAYER_CONNECTED)
//    }
//
//    @Test
//    fun gameCanceled() {
//        launch()
//
//        goToWaitingRoom()
//
//        guestJoinsGame(PlayerHostTest.Guest1)
//
//        UiUtil.clickOnButton(R.id.cancelGameBtn)
//
//        val messageSent = waitForNextMessageSentByHost()
//        assertThat(messageSent).isInstanceOf(Message.CancelGameMessage::class.java)
//
//        assertCurrentScreenIsHomeSreen()
//    }
//
//    private fun guestRejoinsGame(guest: PlayerHostTest) {
//        val player = getGuest(guest)
//        serverTestStub.connectClientToServer(guest)
//        val gameCommonId = inGameStore.getGame().gameCommonId
//        val rejoinMessage = GuestMessageFactory.createRejoinGameMessage(gameCommonId, player.dwitchId)
//        serverTestStub.guestSendsMessageToServer(guest, rejoinMessage, true)
//
//        val messageSent1 = waitForNextMessageSentByHost()
//        assertThat(messageSent1).isInstanceOf(Message.RejoinGameAckMessage::class.java)
//
//        val messageSent2 = waitForNextMessageSentByHost()
//        assertThat(messageSent2).isInstanceOf(Message.WaitingRoomStateUpdateMessage::class.java)
//    }
}
