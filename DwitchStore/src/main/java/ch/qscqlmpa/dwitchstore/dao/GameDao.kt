package ch.qscqlmpa.dwitchstore.dao

import androidx.room.*
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import ch.qscqlmpa.dwitchstore.InsertGameResult
import ch.qscqlmpa.dwitchstore.model.Game
import ch.qscqlmpa.dwitchstore.model.Player
import ch.qscqlmpa.dwitchstore.model.ResumableGameInfo
import io.reactivex.rxjava3.core.Observable
import java.util.*
import org.joda.time.DateTime

@Dao
internal abstract class GameDao(database: AppRoomDatabase) {

    private val playerDao: PlayerDao = database.playerDao()

    @Insert
    abstract fun insertGame(game: Game): Long

    @Update
    abstract fun updateGame(game: Game)

    @Query("DELETE FROM Game WHERE marked_for_deletion = 1")
    abstract fun deleteGamesMarkedForDeletion()

    @Query(
        """
        UPDATE Game
        SET game_common_id=:gameCommonId
        WHERE id=:gameLocalId
            """
    )
    abstract fun updateGameCommonId(gameLocalId: Long, gameCommonId: Long)

    @Query(
        """
        UPDATE Game
        SET game_state=:gameState
        WHERE id=:gameLocalId
            """
    )
    abstract fun updateGameState(gameLocalId: Long, gameState: String)

    @Query("SELECT * FROM Game WHERE id = :localId")
    abstract fun getGame(localId: Long): Game

    @Query("SELECT game_common_id FROM Game WHERE id = :gameLocalId ")
    abstract fun getGameCommonId(gameLocalId: Long): GameCommonId

    @Query("SELECT name FROM Game WHERE id = :localId ")
    abstract fun getGameName(localId: Long): String

    @Query("SELECT * FROM Game WHERE game_common_id = :gameCommonId")
    abstract fun getGame(gameCommonId: GameCommonId): Game?

    @Query("SELECT local_player_id FROM Game WHERE id = :gameLocalId")
    abstract fun getLocalPlayerId(gameLocalId: Long): Long

    @Query("SELECT * FROM Game WHERE id = :localId")
    abstract fun observeGame(localId: Long): Observable<Game>

    @Query(
        """
        UPDATE Game
        SET game_common_id = :gameCommonId
        WHERE id = :gameLocalId
            """
    )
    abstract fun updateGameWithCommonId(gameLocalId: Long, gameCommonId: GameCommonId)

    @Query(
        """
        DELETE FROM Game
        WHERE id = :gameLocalId
        """
    )
    abstract fun deleteGameAndPlayers(gameLocalId: Long)

    /**
     * Insert game and local player for host in Store.
     * Room requires the method to be "open".
     */
    @Transaction
    open fun insertGameForHost(gameName: String, hostPlayerName: String): InsertGameResult {
        val gameCommonId = GameCommonId(Date().time)
        val game = Game(
            id = 0,
            creationDate = DateTime.now(),
            gameCommonId = gameCommonId,
            name = gameName,
            gameState = null,
            localPlayerLocalId = 0
        )
        val gameLocalId = insertGame(game)

        val player = Player(
            id = 0,
            dwitchId = DwitchPlayerId(0),
            gameLocalId = gameLocalId,
            name = hostPlayerName,
            playerRole = PlayerRole.HOST,
            connected = true,
            ready = true
        )
        val playerLocalId = playerDao.insertPlayer(player)
        playerDao.updatePlayer(player.copy(id = playerLocalId, dwitchId = DwitchPlayerId(playerLocalId)))

        updateGame(game.copy(id = gameLocalId, localPlayerLocalId = playerLocalId))

        return InsertGameResult(gameLocalId, gameCommonId, gameName, playerLocalId)
    }

    /**
     * Insert game and local player for guest in Store.
     * Room requires the method to be "open".
     */
    @Transaction
    open fun insertGameForGuest(
        gameName: String,
        gameCommonId: GameCommonId,
        guestPlayerName: String
    ): InsertGameResult {
        val existingGame = getGame(gameCommonId)
        return if (existingGame != null) {
            InsertGameResult(existingGame)
        } else {
            insertNewGuestGame(gameCommonId, gameName, guestPlayerName)
        }
    }

    @Query(
        """
        UPDATE Game
        SET marked_for_deletion = 1
        WHERE id = :gameLocalId
        """
    )
    abstract fun markGameForDeletion(gameLocalId: Long)

    private fun insertNewGuestGame(
        gameCommonId: GameCommonId,
        gameName: String,
        guestPlayerName: String
    ): InsertGameResult {
        val game = Game(
            id = 0,
            creationDate = DateTime.now(),
            gameCommonId = gameCommonId,
            name = gameName,
            gameState = null,
            localPlayerLocalId = 0
        )
        val gameLocalId = insertGame(game)

        val player = Player(
            id = 0,
            dwitchId = DwitchPlayerId(0),
            gameLocalId = gameLocalId,
            name = guestPlayerName,
            playerRole = PlayerRole.GUEST,
            connected = true,
            ready = false
        )
        val playerLocalId = playerDao.insertPlayer(player)

        updateGame(game.copy(id = gameLocalId, localPlayerLocalId = playerLocalId))

        return InsertGameResult(gameLocalId, gameCommonId, gameName, playerLocalId)
    }

    @Query(
        """
        SELECT game_common_id FROM Game
        WHERE game_state is not null
        ORDER BY creation_date DESC
    """
    )
    abstract fun getGameCommonIdOfResumableGames(): Observable<List<GameCommonId>>

    @Transaction // Ensures that both queries (on Game and Player, resp.) are performed atomically.
    @Query(
        """
        SELECT * FROM ResumableGameInfo
        ORDER BY creationDate DESC
    """
    )
    abstract fun getResumableGamesInfo(): Observable<List<ResumableGameInfo>>
}
