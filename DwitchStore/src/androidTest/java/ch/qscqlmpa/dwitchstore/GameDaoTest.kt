package ch.qscqlmpa.dwitchstore

import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchmodel.game.RoomType
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import ch.qscqlmpa.dwitchstore.model.Game
import ch.qscqlmpa.dwitchstore.model.Player
import org.assertj.core.api.Assertions.assertThat
import org.joda.time.DateTime
import org.junit.Before
import org.junit.Test

internal class GameDaoTest : BaseInstrumentedTest() {

    private val playerName = "Arthur"

    @Before
    override fun setup() {
        super.setup()
        bootstrapDb(this)
    }

    @Test
    fun updateGameRoom() {
        val insertGameResult = gameDao.insertGameForHost(gameName, playerName)
        val gameInserted = gameDao.getGame(insertGameResult.gameLocalId)
        assertThat(gameInserted.currentRoom).isEqualTo(RoomType.WAITING_ROOM)

        gameDao.updateGameRoom(insertGameResult.gameLocalId, RoomType.GAME_ROOM)

        val gameTest = gameDao.getGame(insertGameResult.gameLocalId)
        assertThat(gameTest.currentRoom).isEqualTo(RoomType.GAME_ROOM)
    }

    @Test
    fun testInsertGameForHost_createGame() {
        val insertGameResult = gameDao.insertGameForHost(gameName, playerName)

        val gameRef = Game(
            insertGameResult.gameLocalId,
            DateTime.now(),
            RoomType.WAITING_ROOM,
            GameCommonId(0),
            gameName,
            null,
            insertGameResult.localPlayerLocalId
        )
        val gameTest = gameDao.getGame(insertGameResult.gameLocalId)

        assertThat(gameTest).usingRecursiveComparison().ignoringFields("gameCommonId", "creationDate").isEqualTo(gameRef)
        assertThat(gameTest.gameCommonId).isNotEqualTo(GameCommonId(0))
    }

    @Test
    fun testInsertGameForHost_createLocalPlayer() {
        val insertGameResult = gameDao.insertGameForHost(gameName, playerName)

        val playerRef = Player(
            insertGameResult.localPlayerLocalId,
            DwitchPlayerId(insertGameResult.localPlayerLocalId),
            insertGameResult.gameLocalId,
            playerName,
            PlayerRole.HOST,
            PlayerConnectionState.CONNECTED,
            true
        )
        val playerTest = playerDao.getPlayer(insertGameResult.localPlayerLocalId)

        assertThat(playerTest).usingRecursiveComparison().ignoringFields("dwitchId").isEqualTo(playerRef)
    }

    @Test
    fun testInsertGameForGuest_createGame() {
        val gameCommonId = GameCommonId(12354)
        val insertGameResult = gameDao.insertGameForGuest(gameName, gameCommonId, playerName)

        val gameRef = Game(
            insertGameResult.gameLocalId,
            DateTime.now(),
            RoomType.WAITING_ROOM,
            GameCommonId(0),
            gameName,
            null,
            insertGameResult.localPlayerLocalId
        )
        val gameTest = gameDao.getGame(insertGameResult.gameLocalId)

        assertThat(gameTest).usingRecursiveComparison().ignoringFields("gameCommonId", "creationDate").isEqualTo(gameRef)
        assertThat(gameTest.gameCommonId).isNotEqualTo(GameCommonId(0))
    }

    @Test
    fun testInsertGameForGuest_findExistingGame() {

        // Create game
        val gameCommonId = GameCommonId(12354)
        val gameLocalId = gameDao.insertGameForGuest(gameName, gameCommonId, playerName).gameLocalId
        val gameRef = gameDao.getGame(gameLocalId)

        val insertGameResult = gameDao.insertGameForGuest(gameName, gameCommonId, playerName)

        val gameTest = gameDao.getGame(insertGameResult.gameLocalId)

        assertThat(gameTest).usingRecursiveComparison().ignoringFields("gameCommonId", "creationDate").isEqualTo(gameRef)
    }

    @Test
    fun testInsertGameForGuest_createLocalPlayer() {
        val gameCommonId = GameCommonId(12354)

        val insertGameResult = gameDao.insertGameForGuest(gameName, gameCommonId, playerName)

        val playerRef = Player(
            0,
            DwitchPlayerId(0),
            insertGameResult.gameLocalId,
            playerName,
            PlayerRole.GUEST,
            PlayerConnectionState.CONNECTED,
            false
        )
        val playerTest = playerDao.getPlayerByName(playerRef.name)

        assertThat(playerTest)
            .usingRecursiveComparison().ignoringFields("id", "dwitchId")
            .isEqualTo(playerRef)
    }

    companion object {

        private fun bootstrapDb(gameDaoTest: GameDaoTest) {
            // Goal is to have different values for game ID and player ID in the tests
            gameDaoTest.db.runInTransaction {
                val gameId = gameDaoTest.gameDao.insertGame(
                    Game(0, DateTime.now(), RoomType.WAITING_ROOM, GameCommonId(1), "", "", 1)
                )
                val player1 = Player(
                    0,
                    DwitchPlayerId(0),
                    gameId,
                    "",
                    PlayerRole.HOST,
                    PlayerConnectionState.CONNECTED,
                    true
                )
                val player2 = Player(
                    0,
                    DwitchPlayerId(0),
                    gameId,
                    "",
                    PlayerRole.GUEST,
                    PlayerConnectionState.CONNECTED,
                    true
                )
                val player1Id = gameDaoTest.playerDao.insertPlayer(player1)
                gameDaoTest.playerDao.insertPlayer(player2)

                gameDaoTest.playerDao.updatePlayer(
                    player1.copy(
                        id = player1Id,
                        gameLocalId = gameId
                    )
                )
            }
        }
    }
}
