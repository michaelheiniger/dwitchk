package ch.qscqlmpa.dwitch.uitests

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import ch.qscqlmpa.dwitch.PlayerHostTest
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.uitests.base.BaseHostTest
import ch.qscqlmpa.dwitch.uitests.utils.WaitingRoomUtil.PLAYER_CONNECTED
import ch.qscqlmpa.dwitch.uitests.utils.WaitingRoomUtil.PLAYER_DISCONNECTED
import ch.qscqlmpa.dwitch.utils.GameRobot
import ch.qscqlmpa.dwitch.utils.PlayerRobot
import ch.qscqlmpa.dwitch.utils.ViewAssertionUtil.withRecyclerView
import ch.qscqlmpa.dwitchgame.ongoinggame.communication.messagefactories.GuestMessageFactory
import ch.qscqlmpa.dwitchcommunication.model.Message
import ch.qscqlmpa.dwitchmodel.game.RoomType
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import org.assertj.core.api.Assertions.assertThat
import org.hamcrest.CoreMatchers.not
import org.junit.Test

class WaitingRoomAsHostTest : BaseHostTest() {

    @Test
    fun goToWaitingRoomScreen() {
        launch()

        goToWaitingRoom()

        assertPlayerInWR(0, hostName)

        onView(withId(R.id.launchGameBtn))
                .check(matches(withText(R.string.wrhf_launch_game_tv)))
                .check(matches(not(isEnabled())))

        val gameTest = inGameStore.getGame()
        GameRobot(gameTest)
                .assertCurrentRoom(RoomType.WAITING_ROOM)
                .assertName(gameName)
                .assertGameState("")

        val allPlayers = inGameStore.getPlayersInWaitingRoom()
        assertThat(allPlayers.size).isEqualTo(1)

        PlayerRobot(allPlayers[0])
                .assertGameLocalId(gameTest.id)
                .assertName(hostName)
                .assertPlayerRole(PlayerRole.HOST)
                .assertState(PlayerConnectionState.CONNECTED)
                .assertReady(true)
    }

    @Test
    fun playersJoinWaitingRoom() {
        launch()

        goToWaitingRoom()

        guestJoinsGame(PlayerHostTest.Guest1)

        guestJoinsGame(PlayerHostTest.Guest2)

        // Players sorted according to their name ASC
        assertPlayerInWR(0, hostName)
        assertPlayerInWR(1, PlayerHostTest.Guest1.name)
        assertPlayerInWR(2, PlayerHostTest.Guest2.name)

        val allPlayers = inGameStore.getPlayersInWaitingRoom()
        assertThat(allPlayers.size).isEqualTo(3)

        PlayerRobot(allPlayers[1])
                .assertName(PlayerHostTest.Guest1.name)
                .assertInGameId(guest1.inGameId) // in-game ID is the same in message and store
                .assertPlayerRole(PlayerRole.GUEST)
                .assertState(PlayerConnectionState.CONNECTED)
                .assertReady(false)

        PlayerRobot(allPlayers[2])
                .assertName(PlayerHostTest.Guest2.name)
                .assertInGameId(guest2.inGameId) // in-game ID is the same in message and store
                .assertPlayerRole(PlayerRole.GUEST)
                .assertState(PlayerConnectionState.CONNECTED)
                .assertReady(false)
    }

    @Test
    fun guestReadyStateIsUpdated() {
        launch()

        goToWaitingRoom()

        guestJoinsGame(PlayerHostTest.Guest1)

        // Players sorted according to their name ASC
        assertPlayerInWR(0, hostName)
        assertPlayerInWR(1, PlayerHostTest.Guest1.name)
        assertPlayerReady(1, false)

        val allPlayersBefore = inGameStore.getPlayersInWaitingRoom()
        PlayerRobot(allPlayersBefore[1])
                .assertName(PlayerHostTest.Guest1.name)
                .assertReady(false)

        val wrStateUpdateMsg = guestBecomesReady(PlayerHostTest.Guest1)
        assertThat(wrStateUpdateMsg.playerList.find { p -> p.name == PlayerHostTest.Guest1.name }!!.ready).isTrue

        assertPlayerReady(1, true)
        val allPlayersAfter = inGameStore.getPlayersInWaitingRoom()
        PlayerRobot(allPlayersAfter[1])
                .assertName(PlayerHostTest.Guest1.name)
                .assertReady(true)
    }

    @Test
    fun guest1LeavesGame() {
        launch()

        goToWaitingRoom()

        guestJoinsGame(PlayerHostTest.Guest1)

        guestJoinsGame(PlayerHostTest.Guest2)

        // Players sorted according to their name ASC
        assertPlayerInWR(0, hostName)
        assertPlayerInWR(1, PlayerHostTest.Guest1.name)
        assertPlayerInWR(2, PlayerHostTest.Guest2.name)

        val wrStateUpdateMessage = guestLeavesGame(PlayerHostTest.Guest1)

        assertThat(wrStateUpdateMessage.playerList.size).isEqualTo(2)
        PlayerRobot(wrStateUpdateMessage.playerList[0])
                .assertName(hostName)
        PlayerRobot(wrStateUpdateMessage.playerList[1])
                .assertName(PlayerHostTest.Guest2.name)

        // Players sorted according to their name ASC
        assertPlayerInWR(0, hostName)
        assertPlayerInWR(1, PlayerHostTest.Guest2.name)

        val allPlayers = inGameStore.getPlayersInWaitingRoom()
        assertThat(allPlayers.size).isEqualTo(2)
    }

    @Test
    fun guest1DisconnectsAndComesBackWaitingRoom() {
        launch()

        goToWaitingRoom()

        guestJoinsGame(PlayerHostTest.Guest1)

        guestJoinsGame(PlayerHostTest.Guest2)

        // Players sorted according to their name ASC
        assertPlayerInWR(0, hostName, PLAYER_CONNECTED)
        assertPlayerInWR(1, PlayerHostTest.Guest1.name, PLAYER_CONNECTED)
        assertPlayerInWR(2, PlayerHostTest.Guest2.name, PLAYER_CONNECTED)

        guestDisconnects(PlayerHostTest.Guest1)

        // Players sorted according to their name ASC
        assertPlayerInWR(0, hostName, PLAYER_CONNECTED)
        assertPlayerInWR(1, PlayerHostTest.Guest1.name, PLAYER_DISCONNECTED)
        assertPlayerInWR(2, PlayerHostTest.Guest2.name, PLAYER_CONNECTED)

        var allPlayers = inGameStore.getPlayersInWaitingRoom()
        assertThat(allPlayers.size).isEqualTo(3)

        PlayerRobot(allPlayers[1])
                .assertName(PlayerHostTest.Guest1.name)
                .assertPlayerRole(PlayerRole.GUEST)
                .assertState(PlayerConnectionState.DISCONNECTED)
                .assertReady(false)

        guestRejoinsGame(PlayerHostTest.Guest1)

        // Players sorted according to their name ASC
        assertPlayerInWR(0, hostName, PLAYER_CONNECTED)
        assertPlayerInWR(1, PlayerHostTest.Guest1.name, PLAYER_CONNECTED)
        assertPlayerInWR(2, PlayerHostTest.Guest2.name, PLAYER_CONNECTED)

        allPlayers = inGameStore.getPlayersInWaitingRoom()
        assertThat(allPlayers.size).isEqualTo(3)

        PlayerRobot(allPlayers[1])
                .assertName(PlayerHostTest.Guest1.name)
                .assertPlayerRole(PlayerRole.GUEST)
                .assertState(PlayerConnectionState.CONNECTED)
                .assertReady(false)
    }

    @Test
    fun gameCanceled() {
        launch()

        goToWaitingRoom()

        guestJoinsGame(PlayerHostTest.Guest1)

        onView(withId(R.id.cancelGameBtn)).perform(click())

        waitForNextMessageSentByHost() as Message.CancelGameMessage

        dudeWaitASec()

        onView(withId(R.id.gameListTv)).check(matches(isDisplayed()))
    }

    private fun assertPlayerReady(position: Int, ready: Boolean) {
        if (ready) {
            onView(withRecyclerView(R.id.playerListRw)
                    .atPositionOnView(position, R.id.playerReadyCkb))
                    .check(matches(isChecked()))
        } else {
            onView(withRecyclerView(R.id.playerListRw)
                .atPositionOnView(position, R.id.playerReadyCkb))
                    .check(matches(not(isChecked())))
        }
    }

    private fun guestRejoinsGame(guest: PlayerHostTest) {
        val player = getGuest(guest)
        serverTestStub.connectClientToServer(guest, false)
        val gameCommonId = inGameStore.getGame().gameCommonId
        val rejoinMessage = GuestMessageFactory.createRejoinGameMessage(gameCommonId, player.inGameId)
        serverTestStub.guestSendsMessageToServer(guest, rejoinMessage, true)
        waitForNextMessageSentByHost() as Message.RejoinGameAckMessage
        waitForNextMessageSentByHost() as Message.WaitingRoomStateUpdateMessage
    }
}
