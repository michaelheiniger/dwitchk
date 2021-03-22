package ch.qscqlmpa.dwitch.uitests.base

import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.uitests.utils.UiUtil
import ch.qscqlmpa.dwitch.uitests.utils.UiUtil.clickOnButton
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
        clickOnButton(R.id.createGameBtn)

        UiUtil.setControlText(R.id.playerNameEdt, hostName)
        UiUtil.setControlText(R.id.gameNameEdt, gameName)

        clickOnButton(R.id.hostGameBtn)

        dudeWaitAMillisSec()

        /*
        * Note: It also allows to wait for the waiting room to be displayed: otherwise, the messages sent by clients could be
        * missed because the server is not ready yet.
        */
        UiUtil.assertControlTextContent(R.id.playerListTv, R.string.wra_player_list)

        hookOngoingGameDependenciesForHost()

        host = inGameStore.getPlayer(hostName)!! // TODO: delete. No need to check DB stuff in UI tests
    }

    protected fun guestJoinsGame(guest: PlayerHostTest) {
        serverTestStub.connectClientToServer(guest)
        dudeWaitAMillisSec()
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
        serverTestStub.guestSendsMessageToServer(identifier, GuestMessageFactory.createLeaveGameMessage(guest.dwitchId), true)
        val messageSent = waitForNextMessageSentByHost()
        assertThat(messageSent).isInstanceOf(Message.WaitingRoomStateUpdateMessage::class.java)
    }

    private fun assertGuestHasJoinedGame() {
        val message1 = waitForNextMessageSentByHost()
        assertThat(message1).isInstanceOf(Message.JoinGameAckMessage::class.java)
        val message2 = waitForNextMessageSentByHost()
        assertThat(message2).isInstanceOf(Message.WaitingRoomStateUpdateMessage::class.java)
    }

    /**
     * Start observing messages and then perform the action. This is needed for synchronous operations like UI actions
     */
    protected fun waitForNextMessageSentByHost(): Message {
        Logger.debug { "Waiting for next message sent by host..." }
        val messageSerialized =
            Observable.merge(listOf(serverTestStub.observeMessagesSent(), serverTestStub.observeMessagesBroadcasted()))
                .take(1)
                .timeout(10, TimeUnit.SECONDS)
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
                .timeout(10 * numMessagesExpected, TimeUnit.SECONDS)
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
}
