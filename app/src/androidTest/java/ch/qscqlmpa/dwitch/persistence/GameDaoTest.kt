package ch.qscqlmpa.dwitch.persistence

import ch.qscqlmpa.dwitch.BaseInstrumentedTest
import ch.qscqlmpa.dwitch.model.RoomType
import ch.qscqlmpa.dwitch.model.game.Game
import ch.qscqlmpa.dwitch.model.player.Player
import ch.qscqlmpa.dwitch.model.player.PlayerConnectionState
import ch.qscqlmpa.dwitch.model.player.PlayerRole
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import org.assertj.core.api.Assertions.assertThat
import org.junit.Before
import org.junit.Test

class GameDaoTest : BaseInstrumentedTest() {

    private val hostPlayerName = "Arthur"
    private val hostIpAddress = "127.0.0.1"
    private val hostPort = 8889

    private val guestPlayerName = "Lancelot"

    @Before
    override fun setup() {
        super.setup()
        bootstrapDb()
    }

    @Test
    fun updateGameRoom() {
        val insertGameResult = gameDao.insertGameForHost(gameName, hostPlayerName, hostIpAddress, hostPort)
        val gameInserted = gameDao.getGame(insertGameResult.gameLocalId)

        assertThat(gameInserted.currentRoom).isEqualTo(RoomType.WAITING_ROOM)

        gameDao.updateGameRoom(insertGameResult.gameLocalId, RoomType.GAME_ROOM)

        val gameTest = gameDao.getGame(insertGameResult.gameLocalId)

        assertThat(gameTest.currentRoom).isEqualTo(RoomType.GAME_ROOM)
    }

    @Test
    fun testInsertGameForHost_CreateGame() {
        val insertGameResult = gameDao.insertGameForHost(gameName, hostPlayerName, hostIpAddress, hostPort)

        val gameRef = Game(insertGameResult.gameLocalId, RoomType.WAITING_ROOM, insertGameResult.gameLocalId, gameName, "", insertGameResult.localPlayerLocalId, hostIpAddress, hostPort)
        val gameTest = gameDao.getGame(insertGameResult.gameLocalId)

        assertThat(gameRef).isEqualTo(gameTest)
        assertThat(gameRef.localPlayerLocalId).isNotNull()
    }

    @Test
    fun testInsertGameForHost_CreateLocalPlayer() {
        val insertGameResult = gameDao.insertGameForHost(gameName, hostPlayerName, hostIpAddress, hostPort)

        val playerRef = Player(
                insertGameResult.localPlayerLocalId,
                PlayerInGameId(insertGameResult.localPlayerLocalId), insertGameResult.gameLocalId, hostPlayerName,
                PlayerRole.HOST,
                PlayerConnectionState.CONNECTED, true
        )
        val playerTest = playerDao.getPlayer(insertGameResult.localPlayerLocalId)

        assertThat(playerTest).isEqualToIgnoringGivenFields(playerRef, "inGameId")
        assertThat(playerTest.inGameId.value).isNotNull()
    }

    @Test
    fun testInsertGameForGuest_CreateGame() {
        val insertGameResult = gameDao.insertGameForGuest(gameName, guestPlayerName, hostIpAddress, hostPort)

        val gameRef = Game(insertGameResult.gameLocalId, RoomType.WAITING_ROOM, 0, gameName, "", insertGameResult.localPlayerLocalId, hostIpAddress, hostPort)
        val gameTest = gameDao.getGame(insertGameResult.gameLocalId)

        assertThat(gameRef).isEqualTo(gameTest)
        assertThat(gameRef.localPlayerLocalId).isNotNull()
    }

    @Test
    fun testInsertGameForGuest_CreateLocalPlayer() {
        val insertGameResult = gameDao.insertGameForGuest(gameName, guestPlayerName, hostIpAddress, hostPort)

        val playerRef = Player(0, PlayerInGameId(0), insertGameResult.gameLocalId, guestPlayerName, PlayerRole.GUEST,
                PlayerConnectionState.CONNECTED, false)
        val playerTest = playerDao.getPlayerByName(playerRef.name)

        assertThat(playerTest).isEqualToIgnoringGivenFields(playerRef, "id", "inGameId")
        assertThat(playerTest!!.id).isNotNull()
    }

    private fun bootstrapDb() {
        // Goal is to have different values for game ID and player ID in the tests
        db.runInTransaction {
            val gameId = gameDao.insertGame(Game(0, RoomType.WAITING_ROOM, 1, "", "", 1, "192.168.1.1", 8889))
            val player1 = Player(0, PlayerInGameId(0), gameId, "", PlayerRole.HOST, PlayerConnectionState.CONNECTED, true)
            val player2 = Player(0, PlayerInGameId(0), gameId, "", PlayerRole.GUEST, PlayerConnectionState.CONNECTED, true)
            val player1Id = playerDao.insertPlayer(player1)
            playerDao.insertPlayer(player2)

            playerDao.updatePlayer(player1.copy(id = player1Id, gameLocalId = gameId))
        }
    }
}