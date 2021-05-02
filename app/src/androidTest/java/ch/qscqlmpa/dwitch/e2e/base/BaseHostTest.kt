package ch.qscqlmpa.dwitch.e2e.base

import androidx.compose.ui.test.onNodeWithTag
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextReplacement
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.assertTextIsDisplayedOnce
import ch.qscqlmpa.dwitch.e2e.utils.WaitingRoomUtil.assertPlayerInWr
import ch.qscqlmpa.dwitch.ui.common.UiTags
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchcommunication.websocket.server.test.PlayerHostTest
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.GuestMessageFactory
import ch.qscqlmpa.dwitchstore.model.Player
import io.reactivex.rxjava3.core.Observable
import org.assertj.core.api.Assertions.assertThat
import org.tinylog.kotlin.Logger
import java.util.concurrent.TimeUnit

abstract class BaseHostTest : BaseOnGoingGameTest() {

    protected lateinit var host: Player
    protected lateinit var guest1: Player
    protected lateinit var guest2: Player
    protected lateinit var guest3: Player

    protected open fun goToWaitingRoom() {
        testRule.onNodeWithText(getString(R.string.create_game)).performClick()

        testRule.onNodeWithTag(UiTags.playerName).performTextReplacement(hostName)
        testRule.onNodeWithTag(UiTags.gameName).performTextReplacement(gameName)

        testRule.onNodeWithText(getString(R.string.host_game)).performClick()

        incrementGameIdlingResource() // Starting server: ServerCommunicationEvent.ListeningForConnections

        waitForServiceToBeStarted()
        hookOngoingGameDependenciesForHost()

//        app.waitForGameToBeCreated()

        // Assert that the host is indeed in the WaitingRoom
        testRule.assertTextIsDisplayedOnce(getString(R.string.players_in_waitingroom))

        testRule.assertPlayerInWr(hostName)

//        testRule.waitForIdle()

//        hookOngoingGameDependenciesForHost()
    }

    protected fun guestJoinsGame(guest: PlayerHostTest) {
        serverTestStub.connectClientToServer(guest)
        serverTestStub.guestSendsMessageToServer(guest, GuestMessageFactory.createJoinGameMessage(guest.name))
        assertGuestHasJoinedGame()

        when (guest) {
            PlayerHostTest.Guest1 -> guest1 = inGameStore.getPlayer(PlayerHostTest.Guest1.name)!!
            PlayerHostTest.Guest2 -> guest2 = inGameStore.getPlayer(PlayerHostTest.Guest2.name)!!
            PlayerHostTest.Guest3 -> guest3 = inGameStore.getPlayer(PlayerHostTest.Guest3.name)!!
        }
    }

    protected fun guestBecomesReady(identifier: PlayerHostTest) {
        val guest = getGuest(identifier)
        serverTestStub.guestSendsMessageToServer(
            identifier,
            GuestMessageFactory.createPlayerReadyMessage(guest.dwitchId, ready = true)
        )
        val messageSent = waitForNextMessageSentByHost()
        assertThat(messageSent).isInstanceOf(Message.WaitingRoomStateUpdateMessage::class.java)
    }

    protected fun guestDisconnects(identifier: PlayerHostTest) {
        serverTestStub.disconnectFromServer(identifier)
        val messageSent = waitForNextMessageSentByHost()
        assertThat(messageSent).isInstanceOf(Message.WaitingRoomStateUpdateMessage::class.java)
    }

    protected fun guestLeavesGame(identifier: PlayerHostTest) {
        val guest = getGuest(identifier)
        serverTestStub.guestSendsMessageToServer(identifier, GuestMessageFactory.createLeaveGameMessage(guest.dwitchId))
        val messageSent = waitForNextMessageSentByHost()
        assertThat(messageSent).isInstanceOf(Message.WaitingRoomStateUpdateMessage::class.java)
    }

    /**
     * Start observing messages and then perform the action. This is needed for synchronous operations like UI actions
     */
    protected fun waitForNextMessageSentByHost(): Message {
        Logger.debug { "Waiting for next message sent by host..." }
        val messageSerialized =
            Observable.merge(listOf(serverTestStub.observeMessagesSent(), serverTestStub.observeMessagesBroadcasted()))
                .take(1)
                .timeout(3, TimeUnit.SECONDS)
                .blockingFirst()
        val message = commSerializerFactory.unserializeMessage(messageSerialized)
        Logger.debug { "Message sent to client: $message" }
        return message
    }

    protected fun waitForNextNMessageSentByHost(numMessagesExpected: Long): List<Message> {
        Logger.debug { "Waiting for next $numMessagesExpected messages sent by host..." }
        val messagesSerialized =
            Observable.merge(listOf(serverTestStub.observeMessagesSent(), serverTestStub.observeMessagesBroadcasted()))
                .take(numMessagesExpected)
                .timeout(3 * numMessagesExpected, TimeUnit.SECONDS)
                .scan(
                    mutableListOf<String>(),
                    { messages, lastMessage ->
                        Logger.error { "Message sent by host: $lastMessage" }
                        messages.add(lastMessage)
                        messages
                    }
                )
                .blockingLast()

        Logger.debug { "Messages sent to client: $messagesSerialized" }
        return messagesSerialized.map(commSerializerFactory::unserializeMessage)
    }

    protected fun getGuest(identifier: PlayerHostTest): Player {
        return when (identifier) {
            PlayerHostTest.Guest1 -> guest1
            PlayerHostTest.Guest2 -> guest2
            PlayerHostTest.Guest3 -> guest3
        }
    }

    private fun assertGuestHasJoinedGame() {
        val messages = waitForNextNMessageSentByHost(2)
        assertThat(messages[0]).isInstanceOf(Message.JoinGameAckMessage::class.java)
        assertThat(messages[1]).isInstanceOf(Message.WaitingRoomStateUpdateMessage::class.java)
    }
}
