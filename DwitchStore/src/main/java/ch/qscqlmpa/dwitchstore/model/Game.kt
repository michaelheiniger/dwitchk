package ch.qscqlmpa.dwitchstore.model

import androidx.room.*
import ch.qscqlmpa.dwitchmodel.game.GameCommonId
import ch.qscqlmpa.dwitchmodel.game.RoomType
import org.joda.time.DateTime

@Entity(
    tableName = "game",
    foreignKeys = [
        ForeignKey(
            entity = Player::class,
            parentColumns = ["id"],
            childColumns = ["local_player_id"],
            onDelete = ForeignKey.CASCADE,
            deferred = true
        )
    ],
    indices = [
        Index(value = ["local_player_id"], unique = true),
        Index(value = ["game_common_id"], unique = true)
    ]
)
data class Game(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,

    @ColumnInfo(name = "creation_date") val creationDate: DateTime,

    @ColumnInfo(name = "current_room") val currentRoom: RoomType,

    /**
     * ID of the game (unrelated to DB) common to all players
     */
    @ColumnInfo(name = "game_common_id") val gameCommonId: GameCommonId,

    /**
     * Name of the game in the game list
     */
    @ColumnInfo(name = "name") val name: String,

    /**
     * State of the game common to all players.
     */
    @ColumnInfo(name = "game_state") val gameState: String?,

    /**
     * Id of the PlayerPersist record corresponding to the local player
     */
    @ColumnInfo(name = "local_player_id") val localPlayerLocalId: Long
) {
    fun isNew(): Boolean {
        return gameState == null
    }
}
