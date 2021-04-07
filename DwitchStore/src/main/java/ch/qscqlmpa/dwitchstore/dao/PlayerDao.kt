package ch.qscqlmpa.dwitchstore.dao

import androidx.room.*
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import ch.qscqlmpa.dwitchstore.model.Player
import io.reactivex.rxjava3.core.Flowable
import io.reactivex.rxjava3.core.Observable

@Dao
internal interface PlayerDao {

    @Insert
    fun insertPlayer(player: Player): Long

    @Insert
    fun insertPlayers(players: List<Player>): List<Long>

    @Transaction
    fun insertNewGuestPlayer(gameLocalId: Long, name: String): Long {
        val player = Player(
            0,
            DwitchPlayerId(0),
            gameLocalId,
            name,
            PlayerRole.GUEST,
            PlayerConnectionState.CONNECTED,
            false
        )
        val playerLocalId = insertPlayer(player)
        updatePlayer(player.copy(id = playerLocalId, dwitchId = DwitchPlayerId(playerLocalId)))
        return playerLocalId
    }

    @Update
    fun updatePlayer(player: Player)

    @Query("UPDATE Player SET ready = :ready WHERE dwitch_id = :dwitchPlayerId")
    fun updatePlayerWithReady(dwitchPlayerId: DwitchPlayerId, ready: Boolean): Int

    @Query("UPDATE Player SET ready = :ready WHERE id = :playerLocalId")
    fun updatePlayerWithReady(playerLocalId: Long, ready: Boolean): Int

    @Query(
        """
            UPDATE Player 
            SET connectionState = :state, ready = :ready
            WHERE dwitch_id = :dwitchPlayerId
            """
    )
    fun updatePlayer(dwitchPlayerId: DwitchPlayerId, state: PlayerConnectionState, ready: Boolean): Int

    @Query("UPDATE Player SET connectionState = :state, ready = :ready WHERE dwitch_id = :dwitchPlayerId")
    fun updatePlayerWithStateAndReady(dwitchPlayerId: DwitchPlayerId, state: PlayerConnectionState, ready: Boolean): Int

    @Query("UPDATE Player SET connectionState = :state, ready = :ready WHERE id = :playerLocalId")
    fun updatePlayerWithConnectionStateAndReady(playerLocalId: Long, state: PlayerConnectionState, ready: Boolean): Int

    @Query("UPDATE Player SET connectionState = :state WHERE id = :playerLocalId")
    fun updatePlayerWithConnectionState(playerLocalId: Long, state: PlayerConnectionState): Int

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
        WHERE game_local_id = :gameLocalId
        AND dwitch_id = :dwitchPlayerId
        """
    )
    fun deletePlayer(gameLocalId: Long, dwitchPlayerId: DwitchPlayerId): Int

    @Query(
        """
        DELETE FROM Player
        WHERE game_local_id = :gameLocalId
        """
    )
    fun deletePlayers(gameLocalId: Long): Int

    @Query("SELECT * FROM Player WHERE id = :playerLocalId")
    fun gePlayer(playerLocalId: Long): Player

    @Query("SELECT * FROM Player WHERE id = :playerLocalId")
    fun observePlayer(playerLocalId: Long): Observable<Player>

    @Query(
        """
        SELECT * FROM Player
        WHERE game_local_id = :gameLocalId
        AND dwitch_id = :dwitchPlayerId
            """
    )
    fun getPlayer(gameLocalId: Long, dwitchPlayerId: DwitchPlayerId): Player

    @Query(
        """
        SELECT Player.* FROM Player
        WHERE id = :playerLocalId
        """
    )
    fun getPlayer(playerLocalId: Long): Player

    @Query(
        """
        SELECT dwitch_id FROM Player
        WHERE id = :playerLocalId
        """
    )
    fun getPlayerDwitchId(playerLocalId: Long): DwitchPlayerId

    // For test purpose only
    @Query(
        """
        SELECT Player.* FROM Player
        WHERE name = :name
        """
    )
    fun getPlayer(name: String): Player?

    /**
     * @return the list of players that are in the currently active game
     */
    @Query(
        """
        SELECT Player.* FROM Player
        WHERE game_local_id = :gameLocalId
        ORDER BY Player.name ASC
        """
    )
    fun observePlayersInWaitingRoom(gameLocalId: Long): Flowable<List<Player>>

    @Query(
        """
        SELECT * FROM Player
        WHERE game_local_id = :gameLocalId
        ORDER BY name ASC
        """
    )
    fun getPlayersInWaitingRoom(gameLocalId: Long): List<Player>

    @Query(
        """
        UPDATE Player
        SET dwitch_id = :dwitchPlayerId
        WHERE id = :playerLocalId
        """
    )
    fun updatePlayerWithDwitchId(playerLocalId: Long, dwitchPlayerId: DwitchPlayerId): Int

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
        WHERE name = :name
        ORDER BY Player.name
        """
    )
    fun getPlayerByName(name: String): Player?

    @Query(
        """
       UPDATE Player
        SET connectionState = "DISCONNECTED", ready = 0
        WHERE game_local_id = :gameId
        AND id != :localPlayerId
        """
    )
    fun prepareGuestsForGameResume(gameId: Long, localPlayerId: Long)

    @Query(
        """
            SELECT id FROM Player
            WHERE dwitch_id = :dwitchId
            AND game_local_id = :gameLocalId            
        """
    )
    fun getPlayerLocalId(gameLocalId: Long, dwitchId: DwitchPlayerId): Long?
}
