package ch.qscqlmpa.dwitch.e2e.base

import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.assertTextIsDisplayedOnce
import ch.qscqlmpa.dwitch.e2e.utils.WaitingRoomUtil.assertPlayerInWr
import ch.qscqlmpa.dwitch.ui.common.UiTags
import ch.qscqlmpa.dwitchcommunication.deviceconnectivity.DeviceConnectionState
import ch.qscqlmpa.dwitchcommunication.ingame.model.Message
import ch.qscqlmpa.dwitchcommunication.ingame.websocket.server.test.PlayerHostTest
import ch.qscqlmpa.dwitchgame.ingame.communication.messagefactories.GuestMessageFactory
import ch.qscqlmpa.dwitchstore.model.Player
import org.assertj.core.api.Assertions.assertThat
import org.tinylog.kotlin.Logger

abstract class BaseHostTest : BaseOnGoingGameTest() {

    protected lateinit var host: Player
    protected lateinit var guest1: Player
    protected lateinit var guest2: Player
    protected lateinit var guest3: Player

    protected open fun goToWaitingRoom() {
        setCurrentDeviceConnectionState(DeviceConnectionState.ConnectedToWlan("192.168.1.2"))
        testRule.onNodeWithTag(UiTags.createGame).performClick()

        testRule.onNodeWithTag(UiTags.playerName).performTextReplacement(hostName)
        testRule.onNodeWithTag(UiTags.gameName).performTextReplacement(gameName)

        testRule.onNodeWithText(getString(R.string.host_game)).performClick()

        testRule.waitForIdle() // Can't hook on-going game dependencies before component is created
        hookOngoingGameDependenciesForHost()

        // Assert that the host is indeed in the WaitingRoom
        testRule.assertTextIsDisplayedOnce(getString(R.string.players_in_waitingroom))

        testRule.assertPlayerInWr(hostName)
    }

    protected fun guestJoinsGame(guest: PlayerHostTest) {
        serverTestStub.connectClientToServer(guest)
        incrementGameIdlingResource("Guest joins game ($guest)")
        serverTestStub.clientSendsMessageToServer(guest, GuestMessageFactory.createJoinGameMessage(guest.name))
        assertHostSendsMessageFollowingGuestJoiningGame()

        when (guest) {
            PlayerHostTest.Guest1 -> guest1 = inGameStore.getPlayer(PlayerHostTest.Guest1.name)!!
            PlayerHostTest.Guest2 -> guest2 = inGameStore.getPlayer(PlayerHostTest.Guest2.name)!!
            PlayerHostTest.Guest3 -> guest3 = inGameStore.getPlayer(PlayerHostTest.Guest3.name)!!
        }
    }

    protected fun guestBecomesReady(identifier: PlayerHostTest) {
        val guest = getGuest(identifier)
        incrementGameIdlingResource("Guest becomes ready ($guest)")
        serverTestStub.clientSendsMessageToServer(
            identifier,
            GuestMessageFactory.createPlayerReadyMessage(guest.dwitchId, ready = true)
        )
        val messageSent = waitForNextMessageSentByHost()
        assertThat(messageSent).isInstanceOf(Message.WaitingRoomStateUpdateMessage::class.java)
    }

    protected fun guestDisconnects(identifier: PlayerHostTest) {
        incrementGameIdlingResource("Guest disconnects (${getGuest(identifier)})")
        serverTestStub.clientDisconnectsFromServer(identifier)
        val messageSent = waitForNextMessageSentByHost()
        assertThat(messageSent).isInstanceOf(Message.WaitingRoomStateUpdateMessage::class.java)
    }

    protected fun guestLeavesGame(identifier: PlayerHostTest) {
        val guest = getGuest(identifier)
        incrementGameIdlingResource("Guest leaves game ($guest)")
        serverTestStub.clientSendsMessageToServer(identifier, GuestMessageFactory.createLeaveGameMessage(guest.dwitchId))
        val messageSent = waitForNextMessageSentByHost()
        assertThat(messageSent).isInstanceOf(Message.WaitingRoomStateUpdateMessage::class.java)
    }

    /**
     * Start observing messages and then perform the action. This is needed for synchronous operations like UI actions
     */
    protected fun waitForNextMessageSentByHost(): Message {
        Logger.debug { "Waiting for next message sent by host..." }
        val messageSerialized = serverTestStub.blockUntilMessageSentIsAvailable()
        return commSerializerFactory.unserializeMessage(messageSerialized)
    }

    protected fun waitForNextNMessageSentByHost(numMessagesExpected: Long): List<Message> {
        Logger.debug { "Waiting for next $numMessagesExpected messages sent by host..." }
        val messagesSerialized = mutableListOf<String>()
        for (i in 1..numMessagesExpected) messagesSerialized.add(serverTestStub.blockUntilMessageSentIsAvailable())
        return messagesSerialized.map(commSerializerFactory::unserializeMessage)
    }

    protected fun getGuest(identifier: PlayerHostTest): Player {
        return when (identifier) {
            PlayerHostTest.Guest1 -> guest1
            PlayerHostTest.Guest2 -> guest2
            PlayerHostTest.Guest3 -> guest3
        }
    }

    private fun assertHostSendsMessageFollowingGuestJoiningGame() {
        val messages = waitForNextNMessageSentByHost(2)
        assertThat(messages[0]).isInstanceOf(Message.JoinGameAckMessage::class.java)
        assertThat(messages[1]).isInstanceOf(Message.WaitingRoomStateUpdateMessage::class.java)
    }
}
