package ch.qscqlmpa.dwitch.acceptancetests

import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.matcher.ViewMatchers.*
import ch.qscqlmpa.dwitch.Guest1
import ch.qscqlmpa.dwitch.Guest2
import ch.qscqlmpa.dwitch.R
import ch.qscqlmpa.dwitch.components.ongoinggame.messages.Message
import ch.qscqlmpa.dwitch.model.RoomType
import ch.qscqlmpa.dwitch.model.player.PlayerConnectionState
import ch.qscqlmpa.dwitch.model.player.PlayerRole
import ch.qscqlmpa.dwitch.utils.GameRobot
import ch.qscqlmpa.dwitch.utils.PlayerRobot
import ch.qscqlmpa.dwitch.utils.ViewAssertionUtil
import org.hamcrest.Matchers.not
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test


class WaitingRoomAsHostTest : BaseHostTest() {

    @Before
    override fun setup() {
        super.setup()
    }

    @Test
    fun goToWaitingRoomScreen() {
        launch()

        goToWaitingRoom()

        assertPlayerNameInWR(0, hostName)

        onView(withId(R.id.launchGameBtn))
                .check(matches(withText(R.string.wrhf_launch_game_tv)))
                .check(matches(not(isEnabled())))

        val games = gameDao.getAllGames()
        assertEquals(games.size, 1)

        val gameTest = games[0]
        GameRobot(gameTest)
                .assertCurrentRoom(RoomType.WAITING_ROOM)
                .assertName(gameName)
                .assertGameState("")

        val allPlayers = playerDao.getAllPlayersSortedOnNameAsc()
        assertEquals(allPlayers.size, 1)

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

        guestJoinsGame(Guest1)

        guestJoinsGame(Guest2)

        // Players sorted according to their name ASC
        assertPlayerNameInWR(0, hostName)
        assertPlayerNameInWR(1, Guest1.name)
        assertPlayerNameInWR(2, Guest2.name)

        val allPlayers = playerDao.getAllPlayersSortedOnNameAsc()
        assertEquals(allPlayers.size, 3)

        PlayerRobot(allPlayers[1])
                .assertName(Guest1.name)
                .assertInGameId(guest1.inGameId) // in-game ID is the same in message and store
                .assertPlayerRole(PlayerRole.GUEST)
                .assertState(PlayerConnectionState.CONNECTED)
                .assertReady(false)

        PlayerRobot(allPlayers[2])
                .assertName(Guest2.name)
                .assertInGameId(guest2.inGameId) // in-game ID is the same in message and store
                .assertPlayerRole(PlayerRole.GUEST)
                .assertState(PlayerConnectionState.CONNECTED)
                .assertReady(false)
    }

    @Test
    fun guestReadyStateIsUpdated() {
        launch()

        goToWaitingRoom()

        guestJoinsGame(Guest1)

        // Players sorted according to their name ASC
        assertPlayerNameInWR(0, hostName)
        assertPlayerNameInWR(1, Guest1.name)
        assertPlayerReady(1, false)

        var allPlayers = playerDao.getAllPlayersSortedOnNameAsc()
        PlayerRobot(allPlayers[1])
                .assertName(Guest1.name)
                .assertReady(false)

        val wrStateUpdateMessage = guestBecomesReady(Guest1)
        assertTrue(wrStateUpdateMessage.playerList.find { p -> p.name == Guest1.name }!!.ready)

        assertPlayerReady(1, true)
        allPlayers = playerDao.getAllPlayersSortedOnNameAsc()
        PlayerRobot(allPlayers[1])
                .assertName(Guest1.name)
                .assertReady(true)
    }

    @Test
    fun guest1LeavesGame() {
        launch()

        goToWaitingRoom()

        guestJoinsGame(Guest1)

        guestJoinsGame(Guest2)

        // Players sorted according to their name ASC
        assertPlayerNameInWR(0, hostName)
        assertPlayerNameInWR(1, Guest1.name)
        assertPlayerNameInWR(2, Guest2.name)

        val wrStateUpdateMessage = guestLeavesGame(Guest1)

        assertEquals(2, wrStateUpdateMessage.playerList.size)
        PlayerRobot(wrStateUpdateMessage.playerList[0])
                .assertName(hostName)
        PlayerRobot(wrStateUpdateMessage.playerList[1])
                .assertName(Guest2.name)

        // Players sorted according to their name ASC
        assertPlayerNameInWR(0, hostName)
        assertPlayerNameInWR(1, Guest2.name)

        val allPlayers = playerDao.getAllPlayersSortedOnNameAsc()
        assertEquals(allPlayers.size, 2)
    }

    @Test
    fun guest1DisconnectsAndComesBackWaitingRoom() {
        launch()

        goToWaitingRoom()

        guestJoinsGame(Guest1)

        guestJoinsGame(Guest2)

        // Players sorted according to their name ASC
        assertPlayerNameInWR(0, hostName)
        assertPlayerNameInWR(1, Guest1.name)
        assertPlayerNameInWR(2, Guest2.name)

        guestDisconnects(Guest1)

        // Players sorted according to their name ASC
        assertPlayerNameInWR(0, hostName)
        assertPlayerNameInWR(1, Guest2.name)

        var allPlayers = playerDao.getAllPlayersSortedOnNameAsc()
        assertEquals(allPlayers.size, 3)

        PlayerRobot(allPlayers[1])
                .assertName(Guest1.name)
                .assertPlayerRole(PlayerRole.GUEST)
                .assertState(PlayerConnectionState.DISCONNECTED)
                .assertReady(false)

        guestRejoinsGame(Guest1)

        // Players sorted according to their name ASC
        assertPlayerNameInWR(0, hostName)
        assertPlayerNameInWR(1, Guest1.name)
        assertPlayerNameInWR(2, Guest2.name)

        allPlayers = playerDao.getAllPlayersSortedOnNameAsc()
        assertEquals(3, allPlayers.size)

        PlayerRobot(allPlayers[1])
                .assertName(Guest1.name)
                .assertPlayerRole(PlayerRole.GUEST)
                .assertState(PlayerConnectionState.CONNECTED)
                .assertReady(false)
    }

    @Test
    fun gameCanceled() {
        launch()

        goToWaitingRoom()

        guestJoinsGame(Guest1)

        onView(withId(R.id.cancelGameBtn)).perform(click())

        waitForNextMessageSentByHost() as Message.CancelGameMessage

        dudeWaitAMinute(1)

        onView(withId(R.id.gameListTv)).check(matches(isDisplayed()))
    }

    private fun assertPlayerReady(position: Int, ready: Boolean) {
        if (ready) {
            onView(ViewAssertionUtil.withRecyclerView(R.id.playerListRw)
                    .atPositionOnView(position, R.id.playerReadyCkb))
                    .check(matches(isChecked()))
        } else {
            onView(ViewAssertionUtil.withRecyclerView(R.id.playerListRw)
                    .atPositionOnView(position, R.id.playerReadyCkb))
                    .check(matches(not(isChecked())))
        }
    }
}
