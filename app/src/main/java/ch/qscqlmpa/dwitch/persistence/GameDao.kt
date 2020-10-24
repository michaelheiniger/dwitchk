package ch.qscqlmpa.dwitch.persistence

import androidx.room.*
import ch.qscqlmpa.dwitch.model.InsertGameResult
import ch.qscqlmpa.dwitch.model.RoomType
import ch.qscqlmpa.dwitch.model.game.Game
import ch.qscqlmpa.dwitch.model.player.Player
import ch.qscqlmpa.dwitch.model.player.PlayerConnectionState
import ch.qscqlmpa.dwitch.model.player.PlayerRole
import ch.qscqlmpa.dwitchengine.model.game.GamePhase
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import io.reactivex.Observable

@Dao
abstract class GameDao(database: AppRoomDatabase) {

    private val playerDao: PlayerDao = database.playerDao()

    @Insert
    abstract fun insertGame(game: Game): Long

    @Update
    abstract fun updateGame(game: Game)

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
        SET current_room=:gameRoom
        WHERE id=:gameLocalId
            """
    )
    abstract fun updateGameRoom(gameLocalId: Long, gameRoom: RoomType)

    @Query(
        """
        UPDATE Game
        SET game_state=:gameState
        WHERE id=:gameLocalId
            """
    )
    abstract fun updateGameState(gameLocalId: Long, gameState: String)

    @Query("SELECT * FROM Game WHERE id=:localId")
    abstract fun getGame(localId: Long): Game

    @Query("SELECT * FROM Game WHERE id=:localId")
    abstract fun observeGame(localId: Long): Observable<Game>

    @Query(
        """
        UPDATE Game
        SET game_common_id=:gameCommonId
        WHERE id=:gameLocalId
            """
    )
    abstract fun updateGameWithCommonId(gameLocalId: Long, gameCommonId: Long)

    /**
     * Insert game and local player for host in Store.
     * Room requires the method to be "open".
     */
    @Transaction
    open fun insertGameForHost(
        gameName: String,
        hostPlayerName: String,
        hostIpAddress: String,
        hostPort: Int
    ): InsertGameResult {

        val game = Game(0, RoomType.WAITING_ROOM, 0, gameName, "", 0, hostIpAddress, hostPort)
        val gameLocalId = insertGame(game)

        val player = Player(
            0,
            PlayerInGameId(0),
            gameLocalId,
            hostPlayerName,
            PlayerRole.HOST,
            PlayerConnectionState.CONNECTED,
            true
        )
        val playerLocalId = playerDao.insertPlayer(player)
        playerDao.updatePlayer(
            player.copy(
                id = playerLocalId, inGameId = PlayerInGameId(
                    playerLocalId
                )
            )
        )

        updateGame(
            game.copy(
                id = gameLocalId,
                gameCommonId = gameLocalId,
                localPlayerLocalId = playerLocalId
            )
        )

        return InsertGameResult(gameLocalId, playerLocalId)
    }

    /**
     * Insert game and local player for guest in Store.
     * Room requires the method to be "open".
     */
    @Transaction
    open fun insertGameForGuest(
        gameName: String,
        guestPlayerName: String,
        hostIpAddress: String,
        hostPort: Int
    ): InsertGameResult {

        val game = Game(0, RoomType.WAITING_ROOM, 0, gameName, "", 0, hostIpAddress, hostPort)
        val gameLocalId = insertGame(game)

        val player = Player(
            0,
            PlayerInGameId(0),
            gameLocalId,
            guestPlayerName,
            PlayerRole.GUEST,
            PlayerConnectionState.CONNECTED,
            false
        )
        val playerLocalId = playerDao.insertPlayer(player)

        updateGame(game.copy(id = gameLocalId, localPlayerLocalId = playerLocalId))

        return InsertGameResult(gameLocalId, playerLocalId)
    }

    @Query("DELETE FROM Game WHERE id=:gameLocalId")
    abstract fun deleteGame(gameLocalId: Long)

    // ------------------------------- For testing purpose only ------------------------------- //
    @Query("SELECT * FROM Game ORDER BY id ASC")
    abstract fun getAllGames(): List<Game>

    @Query("SELECT * FROM Game WHERE name=:gameName ORDER BY id ASC")
    abstract fun getGameByName(gameName: String): Game?
}