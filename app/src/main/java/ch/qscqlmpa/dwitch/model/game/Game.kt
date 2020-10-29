package ch.qscqlmpa.dwitch.model.game

import androidx.room.*
import ch.qscqlmpa.dwitch.model.RoomType
import ch.qscqlmpa.dwitch.model.player.Player

@Entity(tableName = "game",
        foreignKeys = [
            ForeignKey(
                    entity = Player::class,
                    parentColumns = ["id"],
                    childColumns = ["local_player_id"],
                    onDelete = ForeignKey.CASCADE,
                    deferred = true
            )
        ],
        indices = [Index(value = ["local_player_id"], unique = true), Index(value = ["game_common_id"], unique = true)])
data class Game(
        @PrimaryKey(autoGenerate = true) val id: Long = 0,

        @ColumnInfo(name = "current_room") val currentRoom: RoomType,

        /**
         * ID of the game (unrelated to DB) common to all players
         */
        @ColumnInfo(name = "game_common_id") val gameCommonId: Long,

        /**
         * Name of the game in the game list
         */
        @ColumnInfo(name = "name") val name: String,

        /**
         * State of the game common to all players
         */
        @ColumnInfo(name = "game_state") val gameState: String,

        /**
         * Id of the PlayerPersist record corresponding to the local player
         */
        @ColumnInfo(name = "local_player_id") val localPlayerLocalId: Long,

        @ColumnInfo(name = "host_ip_address") val hostIpAddress: String,

        @ColumnInfo(name = "host_port") val hostPort: Int
)