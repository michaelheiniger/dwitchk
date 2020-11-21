package ch.qscqlmpa.dwitchstore.db

import androidx.room.*
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import ch.qscqlmpa.dwitchmodel.player.Player
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import io.reactivex.Flowable
import io.reactivex.Observable

@Dao
internal interface PlayerDao {

    @Insert
    fun insertPlayer(player: Player): Long

    @Transaction
    fun insertNonLocalPlayer(gameLocalId: Long, player: Player): Long {
        val sanitizedPlayer = player.copy(id = 0, gameLocalId = gameLocalId)
        return insertPlayer(sanitizedPlayer)
    }

    @Transaction
    fun insertNewGuestPlayer(gameLocalId: Long, name: String): Long {
        val player = Player(
            0,
            PlayerInGameId(0),
            gameLocalId,
            name,
            PlayerRole.GUEST,
            PlayerConnectionState.CONNECTED,
            false
        )
        val playerLocalId = insertPlayer(player)
        updatePlayer(
            player.copy(
                id = playerLocalId, inGameId = PlayerInGameId(
                    playerLocalId
                )
            )
        )
        return playerLocalId
    }

    @Update
    fun updatePlayer(player: Player)

    @Query("UPDATE Player SET ready=:ready WHERE in_game_id=:playerInGameId")
    fun updatePlayerWithReady(playerInGameId: PlayerInGameId, ready: Boolean): Int

    @Query("UPDATE Player SET ready=:ready WHERE id=:playerLocalId")
    fun updatePlayerWithReady(playerLocalId: Long, ready: Boolean): Int

    @Query("UPDATE Player SET in_game_id=:inGameId WHERE in_game_id=:playerInGameId")
    fun updatePlayerWithInGameId(playerInGameId: PlayerInGameId, inGameId: PlayerInGameId): Int

    @Query(
        """UPDATE Player 
            SET connectionState=:state, ready=:ready
            WHERE in_game_id=:playerInGameId
            """
    )
    fun updatePlayer(
        playerInGameId: PlayerInGameId,
        state: PlayerConnectionState,
        ready: Boolean
    ): Int

    @Query("UPDATE Player SET connectionState=:state, ready=:ready WHERE in_game_id=:playerInGameId")
    fun updatePlayerWithStateAndReady(
        playerInGameId: PlayerInGameId,
        state: PlayerConnectionState,
        ready: Boolean
    ): Int

    @Query("UPDATE Player SET connectionState=:state, ready=:ready WHERE id=:playerLocalId")
    fun updatePlayerWithConnectionStateAndReady(
        playerLocalId: Long,
        state: PlayerConnectionState,
        ready: Boolean
    ): Int

    @Query(
        """
            UPDATE Player
            SET connectionState = 'DISCONNECTED'
            WHERE game_local_id = :gameLocalId
            """
    )
    fun setAllPlayersToDisconnected(gameLocalId: Long): Int

    @Query(
        """
        DELETE FROM Player
        WHERE id in (:playersLocalId)
        """
    )
    fun deletePlayers(playersLocalId: List<Long>): Int

    @Query(
        """
        DELETE FROM Player
        WHERE game_local_id=:gameLocalId
        AND in_game_id=:playerInGameId
        """
    )
    fun deletePlayer(gameLocalId: Long, playerInGameId: PlayerInGameId): Int

    @Query(
        """
        DELETE FROM Player
        WHERE game_local_id=:gameLocalId
        """
    )
    fun deletePlayers(gameLocalId: Long): Int

    @Query("SELECT * FROM Player WHERE id=:playerLocalId")
    fun gePlayer(playerLocalId: Long): Player

    @Query("SELECT * FROM Player WHERE id=:playerLocalId")
    fun observePlayer(playerLocalId: Long): Observable<Player>

    @Query(
        """
        SELECT * FROM Player
        WHERE game_local_id=:gameLocalId
        AND in_game_id=:playerInGameId
            """
    )
    fun getPlayer(gameLocalId: Long, playerInGameId: PlayerInGameId): Player

    @Query(
        """
        SELECT Player.* FROM Player
        WHERE id =:playerLocalId
        """
    )
    fun getPlayer(playerLocalId: Long): Player

    //For test purpose only
    @Query(
        """
        SELECT Player.* FROM Player
        WHERE name =:name
        """
    )
    fun getPlayer(name: String): Player?


    /**
     * @return the list of players that are in the currently active game
     */
    @Query(
        """
        SELECT Player.* FROM Player
        WHERE game_local_id=:gameLocalId
        ORDER BY Player.name ASC
        """
    )
    fun observePlayersInWaitingRoom(gameLocalId: Long): Flowable<List<Player>>

    @Query(
        """
        SELECT Player.* FROM Player
        WHERE Player.game_local_id=:gameLocalId
        ORDER BY Player.name ASC
        """
    )
    fun getPlayersInWaitingRoom(gameLocalId: Long): List<Player>

    @Query(
        """
        UPDATE Player
        SET in_game_id=:playerInGameId
        WHERE id=:playerLocalId
        """
    )
    fun updatePlayerWithInGameId(playerLocalId: Long, playerInGameId: PlayerInGameId): Int

    // ------------------------------- For testing purpose only ------------------------------- //
    @Query(
        """
        SELECT Player.* FROM Player
        ORDER BY Player.id
        """
    )
    fun getAllPlayersSortedOnIdAsc(): List<Player>

    @Query(
        """
        SELECT Player.* FROM Player
        ORDER BY Player.name
        """
    )
    fun getAllPlayersSortedOnNameAsc(): List<Player>

    @Query(
        """
        SELECT Player.* FROM Player
        WHERE name=:name
        ORDER BY Player.name
        """
    )
    fun getPlayerByName(name: String): Player?

    @Query(
        """
        SELECT Player.* FROM Player
        WHERE in_game_id=:inGameId
        ORDER BY Player.name
        """
    )
    fun getPlayerByInGameId(inGameId: PlayerInGameId): Player
}