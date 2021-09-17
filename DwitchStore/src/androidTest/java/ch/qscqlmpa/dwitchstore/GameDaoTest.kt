package ch.qscqlmpa.dwitchstore

import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import ch.qscqlmpa.dwitchstore.model.Game
import ch.qscqlmpa.dwitchstore.model.Player
import org.assertj.core.api.Assertions.assertThat
import org.joda.time.DateTime
import org.junit.Before
import org.junit.Test
import java.util.*

internal class GameDaoTest : BaseInstrumentedTest() {

    private val playerName = "Arthur"

    @Before
    override fun setup() {
        super.setup()
        bootstrapDb(this)
    }

    @Test
    fun testInsertGameForHost_createGame() {
        val insertGameResult = gameDao.insertGameForHost(gameName, playerName)

        val gameRef = Game(
            insertGameResult.gameLocalId,
            DateTime.now(),
            GameCommonId(UUID.randomUUID()),
            gameName,
            null,
            insertGameResult.localPlayerLocalId
        )
        val gameTest = gameDao.getGame(insertGameResult.gameLocalId)

        assertThat(gameTest).usingRecursiveComparison().ignoringFields("gameCommonId", "creationDate").isEqualTo(gameRef)
        assertThat(gameTest.gameCommonId).isNotEqualTo(gameRef.gameCommonId) // Because the game common ID is supposed to be randomly generated
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
            connected = true,
            ready = true
        )
        val playerTest = playerDao.getPlayer(insertGameResult.localPlayerLocalId)

        assertThat(playerTest).usingRecursiveComparison().ignoringFields("dwitchId").isEqualTo(playerRef)
    }

    @Test
    fun testInsertGameForGuest_createGame() {
        val gameCommonId = GameCommonId(UUID.randomUUID())
        val insertGameResult = gameDao.insertGameForGuest(gameName, gameCommonId, playerName)

        val gameRef = Game(
            insertGameResult.gameLocalId,
            DateTime.now(),
            gameCommonId,
            gameName,
            null,
            insertGameResult.localPlayerLocalId
        )
        val gameTest = gameDao.getGame(insertGameResult.gameLocalId)

        assertThat(gameTest).usingRecursiveComparison().ignoringFields("creationDate").isEqualTo(gameRef)
    }

    @Test
    fun testInsertGameForGuest_findExistingGame() {

        // Create game
        val gameCommonId = GameCommonId(UUID.randomUUID())
        val gameLocalId = gameDao.insertGameForGuest(gameName, gameCommonId, playerName).gameLocalId
        val gameRef = gameDao.getGame(gameLocalId)

        val insertGameResult = gameDao.insertGameForGuest(gameName, gameCommonId, playerName)

        val gameTest = gameDao.getGame(insertGameResult.gameLocalId)

        assertThat(gameTest).usingRecursiveComparison().ignoringFields("creationDate").isEqualTo(gameRef)
    }

    @Test
    fun testInsertGameForGuest_createLocalPlayer() {
        val gameCommonId = GameCommonId(UUID.randomUUID())

        val insertGameResult = gameDao.insertGameForGuest(gameName, gameCommonId, playerName)

        val playerRef = Player(
            0,
            DwitchPlayerId(0),
            insertGameResult.gameLocalId,
            playerName,
            PlayerRole.GUEST,
            connected = true,
            ready = false
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
                    Game(0, DateTime.now(), GameCommonId(UUID.randomUUID()), "", "", 1)
                )
                val player1 = Player(
                    0,
                    DwitchPlayerId(0),
                    gameId,
                    "",
                    PlayerRole.HOST,
                    connected = true,
                    ready = true
                )
                val player2 = Player(
                    0,
                    DwitchPlayerId(0),
                    gameId,
                    "",
                    PlayerRole.GUEST,
                    connected = true,
                    ready = true
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
