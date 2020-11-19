package ch.qscqlmpa.dwitch.uitests

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import ch.qscqlmpa.dwitch.Guest2
import ch.qscqlmpa.dwitch.Guest3
import ch.qscqlmpa.dwitch.PlayerGuestTest
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.model.player.Player
import ch.qscqlmpa.dwitch.model.player.PlayerConnectionState
import ch.qscqlmpa.dwitch.model.player.PlayerRole
import ch.qscqlmpa.dwitch.ongoinggame.messages.Message
import ch.qscqlmpa.dwitch.uitests.base.BaseGuestTest
import ch.qscqlmpa.dwitch.uitests.utils.WaitingRoomUtil.PLAYER_CONNECTED
import ch.qscqlmpa.dwitch.uitests.utils.WaitingRoomUtil.PLAYER_DISCONNECTED
import ch.qscqlmpa.dwitch.utils.PlayerRobot
import org.assertj.core.api.Assertions.assertThat
import org.junit.Assert.assertEquals
import org.junit.Before
import org.junit.Test


class WaitingRoomAsGuestTest : BaseGuestTest() {

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

        dudeWaitASec()

        // Players sorted according to their name ASC
        assertPlayerInWR(0, PlayerGuestTest.Host.name, PLAYER_CONNECTED)
        assertPlayerInWR(1, PlayerGuestTest.LocalGuest.name, PLAYER_CONNECTED)
        assertPlayerInWR(2, PlayerGuestTest.Guest2.name, PLAYER_CONNECTED)
        assertPlayerInWR(3, PlayerGuestTest.Guest3.name, PLAYER_CONNECTED)

        val allPlayers = playerDao.getAllPlayersSortedOnNameAsc()
        assertThat(allPlayers.size).isEqualTo(4)

        PlayerRobot(allPlayers[0])
            .assertName(PlayerGuestTest.Host.name)
            .assertInGameId(PlayerGuestTest.Host.inGameId)
        PlayerRobot(allPlayers[1])
            .assertName(PlayerGuestTest.LocalGuest.name)
            .assertInGameId(PlayerGuestTest.LocalGuest.inGameId)
        PlayerRobot(allPlayers[2])
            .assertName(PlayerGuestTest.Guest2.name)
            .assertInGameId(PlayerGuestTest.Guest2.inGameId)
        PlayerRobot(allPlayers[3])
            .assertName(PlayerGuestTest.Guest3.name)
            .assertInGameId(PlayerGuestTest.Guest3.inGameId)
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

        dudeWaitASec()

        var allPlayers = playerDao.getAllPlayersSortedOnNameAsc()
        assertThat(allPlayers.size).isEqualTo(4)
        PlayerRobot(allPlayers[1])
            .assertName(PlayerGuestTest.LocalGuest.name)
            .assertInGameId(PlayerGuestTest.LocalGuest.inGameId)
            .assertReady(false)

        setLocalPlayerReady()

        clientTestStub.serverSendsMessageToClient(Message.PlayerReadyMessage(PlayerGuestTest.LocalGuest.inGameId, true), false)

        // Players sorted according to their name ASC
        assertPlayerInWR(0, PlayerGuestTest.Host.name, PLAYER_CONNECTED)
        assertPlayerInWR(1, PlayerGuestTest.LocalGuest.name, PLAYER_CONNECTED)
        assertPlayerInWR(2, PlayerGuestTest.Guest2.name, PLAYER_CONNECTED)
        assertPlayerInWR(3, PlayerGuestTest.Guest3.name, PLAYER_CONNECTED)

        allPlayers = playerDao.getAllPlayersSortedOnNameAsc()
        assertEquals(allPlayers.size, 4)

        PlayerRobot(allPlayers[0])
            .assertName(PlayerGuestTest.Host.name)
            .assertInGameId(PlayerGuestTest.Host.inGameId)
        PlayerRobot(allPlayers[1])
            .assertName(PlayerGuestTest.LocalGuest.name)
            .assertInGameId(PlayerGuestTest.LocalGuest.inGameId)
            .assertReady(true)
        PlayerRobot(allPlayers[2])
            .assertName(PlayerGuestTest.Guest2.name)
            .assertInGameId(PlayerGuestTest.Guest2.inGameId)
        PlayerRobot(allPlayers[3])
            .assertName(PlayerGuestTest.Guest3.name)
            .assertInGameId(PlayerGuestTest.Guest3.inGameId)
    }

    @Test
    fun localPlayerGetsDisconnected() {
        launch()

        advertiseGame()

        goToWaitingRoom()

        clientTestStub.connectClientToServer(true)

        assertLocalGuestHasSentJoinGameMessage()

        hostSendsJoinGameAck()
        hostSendsInitialWaitingRoomUpdate()

        dudeWaitASec()

        // Players sorted according to their name ASC
        assertPlayerInWR(0, PlayerGuestTest.Host.name, PLAYER_CONNECTED)
        assertPlayerInWR(1, PlayerGuestTest.LocalGuest.name, PLAYER_CONNECTED)
        assertPlayerInWR(2, PlayerGuestTest.Guest2.name, PLAYER_CONNECTED)
        assertPlayerInWR(3, PlayerGuestTest.Guest3.name, PLAYER_CONNECTED)

        clientTestStub.breakConnectionWithHost()

        dudeWaitASec()

        // Players sorted according to their name ASC
        assertPlayerInWR(0, PlayerGuestTest.Host.name, PLAYER_DISCONNECTED)
        assertPlayerInWR(1, PlayerGuestTest.LocalGuest.name, PLAYER_DISCONNECTED)
        assertPlayerInWR(2, PlayerGuestTest.Guest2.name, PLAYER_DISCONNECTED)
        assertPlayerInWR(3, PlayerGuestTest.Guest3.name, PLAYER_DISCONNECTED)
    }

    @Test
    fun gameCanceled() {
        launch()

        advertiseGame()

        goToWaitingRoom()

        clientTestStub.connectClientToServer(true)

        clientTestStub.serverSendsMessageToClient(Message.CancelGameMessage, false)

        dudeWaitASec()

        onView(withId(R.id.btnOk)).perform(click())

        dudeWaitASec()

        onView(withId(R.id.gameListTv)).check(matches(isDisplayed()))
    }

    private fun hostSendsInitialWaitingRoomUpdate() {
        val gameLocalIdAtHost = 1233L
        val message = Message.WaitingRoomStateUpdateMessage(
            listOf(
                Player(
                    334,
                    PlayerGuestTest.Host.inGameId,
                    gameLocalIdAtHost,
                    PlayerGuestTest.Host.name,
                    PlayerRole.HOST,
                    PlayerConnectionState.CONNECTED,
                    true
                ),
                Player(
                    335,
                    PlayerGuestTest.LocalGuest.inGameId,
                    gameLocalIdAtHost,
                    PlayerGuestTest.LocalGuest.name,
                    PlayerRole.GUEST,
                    PlayerConnectionState.CONNECTED,
                    false
                ),
                Player(
                    336,
                    PlayerGuestTest.Guest2.inGameId,
                    gameLocalIdAtHost,
                    Guest2.name,
                    PlayerRole.GUEST,
                    PlayerConnectionState.CONNECTED,
                    true
                ),
                Player(
                    337,
                    PlayerGuestTest.Guest3.inGameId,
                    gameLocalIdAtHost,
                    Guest3.name,
                    PlayerRole.GUEST,
                    PlayerConnectionState.CONNECTED,
                    true
                )
            )
        )
        clientTestStub.serverSendsMessageToClient(message, false)
    }

}
