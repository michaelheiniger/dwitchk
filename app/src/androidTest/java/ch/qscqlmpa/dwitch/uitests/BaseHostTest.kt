package ch.qscqlmpa.dwitch.uitests

import ch.qscqlmpa.dwitch.*
import ch.qscqlmpa.dwitch.model.player.Player
import ch.qscqlmpa.dwitch.ongoinggame.messages.GuestMessageFactory
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import org.junit.Assert
import timber.log.Timber
import java.util.concurrent.TimeUnit

abstract class BaseHostTest : BaseOnGoingGameTest() {

    protected lateinit var host: Player
    protected lateinit var guest1: Player
    protected lateinit var guest2: Player
    protected lateinit var guest3: Player

    protected open fun goToWaitingRoom() {

        clickOnButton(R.id.createGameBtn)

        setControlText(R.id.playerNameEdt, hostName)
        setControlText(R.id.gameNameEdt, gameName)

        clickOnButton(R.id.nextBtn)

        dudeWaitAMinute(1)

        host = playerDao.getPlayerByName(hostName)!!

        /*
        * Note: It also allows to wait for the waiting room to be displayed: otherwise, the messages sent by clients could be
        * missed because the server is not ready yet.
        */
        assertControlTextContent(R.id.playerListTv, R.string.wra_player_list)

        hookOngoingGameDependenciesForHost()
    }

    protected fun guestJoinsGame(guest: GuestIdTestHost) {
        serverTestStub.connectClientToServer(guest, false)
        serverTestStub.guestSendsMessageToServer(guest, GuestMessageFactory.createJoinGameMessage(guest.name), true)
        assertGuestHasJoinedGame()

        when (guest) {
            Guest1 -> guest1 = playerDao.getPlayerByName(Guest1.name)!!
            Guest2 -> guest2 = playerDao.getPlayerByName(Guest2.name)!!
            Guest3 -> guest3 = playerDao.getPlayerByName(Guest3.name)!!
        }
    }

    protected fun guestRejoinsGame(guest: GuestIdTestHost) {
        val player = getGuest(guest)
        serverTestStub.connectClientToServer(guest, false)
        serverTestStub.guestSendsMessageToServer(guest, GuestMessageFactory.createRejoinGameMessage(player.inGameId), true)
        waitForNextMessageSentByHost() as Message.WaitingRoomStateUpdateMessage
    }

    protected fun guestBecomesReady(identifier: GuestIdTestHost): Message.WaitingRoomStateUpdateMessage {
        val guest = getGuest(identifier)
        serverTestStub.guestSendsMessageToServer(identifier, GuestMessageFactory.createPlayerReadyMessage(guest.inGameId, true), true)
        return waitForNextMessageSentByHost() as Message.WaitingRoomStateUpdateMessage
    }

    protected fun guestDisconnects(identifier: GuestIdTestHost) {
        serverTestStub.disconnectFromServer(identifier, true)
        waitForNextMessageSentByHost() as Message.WaitingRoomStateUpdateMessage
    }

    protected fun guestLeavesGame(identifier: GuestIdTestHost): Message.WaitingRoomStateUpdateMessage {
        val guest = getGuest(identifier)
        serverTestStub.guestSendsMessageToServer(identifier, GuestMessageFactory.createLeaveGameMessage(guest.inGameId), true)
        return waitForNextMessageSentByHost() as Message.WaitingRoomStateUpdateMessage
    }

    protected fun getPlayer(guest: GuestIdTestHost): Player {
        return when (guest) {
            Guest1 -> guest1
            Guest2 -> guest2
            Guest3 -> guest3
        }
    }

    private fun assertGuestHasJoinedGame() {
        val joinGameAckMessageForGuest = waitForNextMessageSentByHost() as Message.JoinGameAckMessage
        Assert.assertNotEquals(0, joinGameAckMessageForGuest.playerInGameId)
        waitForNextMessageSentByHost() as Message.WaitingRoomStateUpdateMessage
    }

    /**
     * Start observing messages and then perform the action. This is needed for synchronous operations like UI actions
     */
    protected fun waitForNextMessageSentByHost(): Message {
        Timber.d("Waiting for next message sent by host...")
        val messageSerialized = serverTestStub.observeMessagesSent()
                .take(1)
                .timeout(10, TimeUnit.SECONDS)
                .blockingFirst()
        val message = serializerFactory.unserializeMessage(messageSerialized)
        Timber.d("Message sent to client: %s", message)
        return message
    }

    protected fun waitForNextNMessagesSentByHost(N: Int): List<Message> {
        assert(N > 0)
        Timber.d("Waiting for the next %d messages sent by host...", N)
        val messages = mutableListOf<Message>()
        for (i in 1..N) {
            val messageSerialized = serverTestStub.observeMessagesSent()
                    .take(1)
                    .timeout(5, TimeUnit.SECONDS)
                    .blockingFirst()
            val message = serializerFactory.unserializeMessage(messageSerialized)
            messages.add(message)
            Timber.d("Message sent to client: %s", message)
        }
        return messages
    }

    private fun getGuest(identifier: GuestIdTestHost): Player {
        return when (identifier) {
            Guest1 -> guest1
            Guest2 -> guest2
            Guest3 -> guest3
        }
    }
}