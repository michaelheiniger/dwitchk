package ch.qscqlmpa.dwitchmodel.player

import androidx.room.*
import ch.qscqlmpa.dwitchengine.model.player.PlayerDwitchId
import ch.qscqlmpa.dwitchengine.model.player.PlayerOnboardingInfo
import ch.qscqlmpa.dwitchmodel.game.Game
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
    @ColumnInfo(name = "dwitch_id") val dwitchId: PlayerDwitchId,
    @ColumnInfo(name = "game_local_id") val gameLocalId: Long,
    val name: String,
    @ColumnInfo(name = "player_role") val playerRole: PlayerRole,
    val connectionState: PlayerConnectionState,
    val ready: Boolean
) {

    fun toPlayerInfo(): PlayerOnboardingInfo {
        return PlayerOnboardingInfo(dwitchId, name)
    }
}