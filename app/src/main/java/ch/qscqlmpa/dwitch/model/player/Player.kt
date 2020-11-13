package ch.qscqlmpa.dwitch.model.player

import androidx.room.*
import ch.qscqlmpa.dwitch.model.game.Game
import ch.qscqlmpa.dwitchengine.model.player.PlayerInGameId
import ch.qscqlmpa.dwitchengine.model.player.PlayerInfo
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
    ], indices = [Index("game_local_id")]
)
@Serializable
data class Player(
    @PrimaryKey(autoGenerate = true) val id: Long,
    @ColumnInfo(name = "in_game_id") val inGameId: PlayerInGameId,
    @ColumnInfo(name = "game_local_id") val gameLocalId: Long,
    val name: String,
    @ColumnInfo(name = "player_role") val playerRole: PlayerRole,
    val connectionState: PlayerConnectionState,
    val ready: Boolean
) {

    fun toPlayerInfo(): PlayerInfo {
        return PlayerInfo(inGameId, name)
    }
}