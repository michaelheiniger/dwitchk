package ch.qscqlmpa.dwitch.uitests.base

import ch.qscqlmpa.dwitch.PlayerHostTest
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.uitests.utils.UiUtil
import ch.qscqlmpa.dwitch.uitests.utils.UiUtil.clickOnButton
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.GuestMessageFactory
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchmodel.player.Player
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

        dudeWaitASec()

        host = inGameStore.getPlayer(hostName)!!

        /*
        * Note: It also allows to wait for the waiting room to be displayed: otherwise, the messages sent by clients could be
        * missed because the server is not ready yet.
        */
        UiUtil.assertControlTextContent(R.id.playerListTv, R.string.wra_player_list)

        hookOngoingGameDependenciesForHost()
    }

    protected fun guestJoinsGame(guest: PlayerHostTest) {
        serverTestStub.connectClientToServer(guest, false)
        serverTestStub.guestSendsMessageToServer(guest, GuestMessageFactory.createJoinGameMessage(guest.name), true)
        assertGuestHasJoinedGame()

        when (guest) {
            PlayerHostTest.Guest1 -> guest1 = inGameStore.getPlayer(PlayerHostTest.Guest1.name)!!
            PlayerHostTest.Guest2 -> guest2 = inGameStore.getPlayer(PlayerHostTest.Guest2.name)!!
            PlayerHostTest.Guest3 -> guest3 = inGameStore.getPlayer(PlayerHostTest.Guest3.name)!!
        }
    }

    protected fun guestBecomesReady(identifier: PlayerHostTest): Message.WaitingRoomStateUpdateMessage {
        val guest = getGuest(identifier)
        serverTestStub.guestSendsMessageToServer(identifier, GuestMessageFactory.createPlayerReadyMessage(guest.inGameId, true), true)
        return waitForNextMessageSentByHost() as Message.WaitingRoomStateUpdateMessage
    }

    protected fun guestDisconnects(identifier: PlayerHostTest) {
        serverTestStub.disconnectFromServer(identifier, true)
        waitForNextMessageSentByHost() as Message.WaitingRoomStateUpdateMessage
    }

    protected fun guestLeavesGame(identifier: PlayerHostTest): Message.WaitingRoomStateUpdateMessage {
        val guest = getGuest(identifier)
        serverTestStub.guestSendsMessageToServer(identifier, GuestMessageFactory.createLeaveGameMessage(guest.inGameId), true)
        return waitForNextMessageSentByHost() as Message.WaitingRoomStateUpdateMessage
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
        val message = commSerializerFactory.unserializeMessage(messageSerialized)
        Timber.d("Message sent to client: $message")
        return message
    }

    protected fun getGuest(identifier: PlayerHostTest): Player {
        return when (identifier) {
            PlayerHostTest.Guest1 -> guest1
            PlayerHostTest.Guest2 -> guest2
            PlayerHostTest.Guest3 -> guest3
        }
    }
}