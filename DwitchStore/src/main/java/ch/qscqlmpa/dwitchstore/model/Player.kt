package ch.qscqlmpa.dwitchstore.model

import androidx.room.*
import ch.qscqlmpa.dwitchengine.model.player.DwitchPlayerId
import ch.qscqlmpa.dwitchmodel.player.PlayerConnectionState
import ch.qscqlmpa.dwitchmodel.player.PlayerRole
import kotlinx.serialization.Serializable

@Entity(
    tableName = "player",
    foreignKeys = [
        ForeignKey(
            entity = Game::class,
            parentColumns = ["id"],
            childColumns = ["game_local_id"],
            onDelete = ForeignKey.CASCADE,
            deferred = true
        )
    ],
    indices = [Index("game_local_id")]
)
@Serializable
data class Player(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "dwitch_id") val dwitchId: DwitchPlayerId,
    @ColumnInfo(name = "game_local_id") val gameLocalId: Long,
    val name: String,
    @ColumnInfo(name = "player_role") val playerRole: PlayerRole,
    val connectionState: PlayerConnectionState,
    val ready: Boolean,
    @ColumnInfo(name = "computer_managed") val computerManaged: Boolean = false
) {
    val isHost: Boolean get() = playerRole == PlayerRole.HOST
}
