package ch.qscqlmpa.dwitch.acceptancetests

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.communication.client.Guest2
import ch.qscqlmpa.dwitch.communication.client.Guest3
import ch.qscqlmpa.dwitch.communication.client.Host
import ch.qscqlmpa.dwitch.communication.client.LocalGuest
import ch.qscqlmpa.dwitch.components.ongoinggame.messages.Message
import ch.qscqlmpa.dwitch.model.player.Player
import ch.qscqlmpa.dwitch.model.player.PlayerConnectionState
import ch.qscqlmpa.dwitch.model.player.PlayerRole
import ch.qscqlmpa.dwitch.utils.PlayerRobot
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


class WaitingRoomAsGuestTest : BaseGuestTest() {

    private val gameCommonId = 34L

    @Before
    override fun setup() {
        super.setup()
    }

    @Test
    fun goToWaitingRoomScreen_AckFromHostReceived() {
        launch()

        advertiseGame()

        goToWaitingRoom()

        clientTestStub.connectClientToServer(true)

        assertLocalGuestHasSentJoinGameMessage()

        hostSendsJoinGameAck()
        hostSendsInitialWaitingRoomUpdate()

        dudeWaitAMinute(2)

        // Players sorted according to their name ASC
        assertPlayerNameInWR(0, Host.name)
        assertPlayerNameInWR(1, LocalGuest.name)
        assertPlayerNameInWR(2, Guest2.name)
        assertPlayerNameInWR(3, Guest3.name)

        val allPlayers = playerDao.getAllPlayersSortedOnNameAsc()
        assertEquals(allPlayers.size, 4)

        PlayerRobot(allPlayers[0])
                .assertName(Host.name)
                .assertInGameId(Host.inGameId)
        PlayerRobot(allPlayers[1])
                .assertName(LocalGuest.name)
                .assertInGameId(LocalGuest.inGameId)
        PlayerRobot(allPlayers[2])
                .assertName(Guest2.name)
                .assertInGameId(Guest2.inGameId)
        PlayerRobot(allPlayers[3])
                .assertName(Guest3.name)
                .assertInGameId(Guest3.inGameId)
    }

    @Test
    fun playerReadyMessageSent() {
        launch()

        advertiseGame()

        goToWaitingRoom()

        clientTestStub.connectClientToServer(true)

        assertLocalGuestHasSentJoinGameMessage()

        hostSendsJoinGameAck()
        hostSendsInitialWaitingRoomUpdate()

        dudeWaitAMinute(2)

        var allPlayers = playerDao.getAllPlayersSortedOnNameAsc()
        assertEquals(allPlayers.size, 4)
        PlayerRobot(allPlayers[1])
                .assertName(LocalGuest.name)
                .assertInGameId(LocalGuest.inGameId)
                .assertReady(false)

        setLocalPlayerReady()

        clientTestStub.serverSendsMessageToClient(Message.PlayerReadyMessage(LocalGuest.inGameId, true), false)

        // Players sorted according to their name ASC
        assertPlayerNameInWR(0, Host.name)
        assertPlayerNameInWR(1, LocalGuest.name)
        assertPlayerNameInWR(2, Guest2.name)
        assertPlayerNameInWR(3, Guest3.name)

        allPlayers = playerDao.getAllPlayersSortedOnNameAsc()
        assertEquals(allPlayers.size, 4)

        PlayerRobot(allPlayers[0])
                .assertName(Host.name)
                .assertInGameId(Host.inGameId)
        PlayerRobot(allPlayers[1])
                .assertName(LocalGuest.name)
                .assertInGameId(LocalGuest.inGameId)
                .assertReady(true)
        PlayerRobot(allPlayers[2])
                .assertName(Guest2.name)
                .assertInGameId(Guest2.inGameId)
        PlayerRobot(allPlayers[3])
                .assertName(Guest3.name)
                .assertInGameId(Guest3.inGameId)
    }

    @Test
    fun gameCanceled() {
        launch()

        advertiseGame()

        goToWaitingRoom()

        clientTestStub.connectClientToServer(true)

        clientTestStub.serverSendsMessageToClient(Message.CancelGameMessage, false)

        dudeWaitAMinute(2)

        onView(withId(R.id.btnDone)).perform(click())

        dudeWaitAMinute(1)

        onView(withId(R.id.gameListTv)).check(matches(isDisplayed()))
    }

    private fun assertLocalGuestHasSentJoinGameMessage() {
        val joinGameMessage = waitForNextMessageSentByLocalGuest() as Message.JoinGameMessage
        assertEquals(LocalGuest.name, joinGameMessage.playerName)
    }

    private fun hostSendsJoinGameAck() {
        val message = Message.JoinGameAckMessage(gameCommonId, LocalGuest.inGameId)
        clientTestStub.serverSendsMessageToClient(message, false)
    }

    private fun hostSendsInitialWaitingRoomUpdate() {
        val gameLocalIdAtHost = 1233L
        val message = Message.WaitingRoomStateUpdateMessage(listOf(
                Player(334, Host.inGameId, gameLocalIdAtHost, Host.name, PlayerRole.HOST, PlayerConnectionState.CONNECTED, true),
                Player(335, LocalGuest.inGameId, gameLocalIdAtHost, LocalGuest.name, PlayerRole.GUEST, PlayerConnectionState.CONNECTED,
                        false),
                Player(336, Guest2.inGameId, gameLocalIdAtHost, Guest2.name, PlayerRole.GUEST, PlayerConnectionState.CONNECTED, true),
                Player(337, Guest3.inGameId, gameLocalIdAtHost, Guest3.name, PlayerRole.GUEST, PlayerConnectionState.CONNECTED, true)
        ))
        clientTestStub.serverSendsMessageToClient(message, false)
    }
}
